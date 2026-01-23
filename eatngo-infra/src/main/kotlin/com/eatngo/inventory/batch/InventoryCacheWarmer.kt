package com.eatngo.inventory.batch

import com.eatngo.common.circuitbreaker.event.CircuitBreakerStateChangedEvent
import com.eatngo.common.circuitbreaker.service.CircuitBreakerManager
import com.eatngo.inventory.infra.InventoryCachePersistence
import com.eatngo.inventory.infra.InventoryPersistence
import com.eatngo.inventory.infra.PartitionedRedisSetRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class InventoryCacheWarmer(
    private val inventoryPersistence: InventoryPersistence,
    private val inventoryCachePersistence: InventoryCachePersistence,
    private val inventoryScope: CoroutineScope,
    private val partitionedRedisSetRepository: PartitionedRedisSetRepository,
    @Value("\${inventory.partition.count:10}")
    private val partitionCount: Int,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val CHANGED_SET_KEY = "changedProductIds"
        private const val PENDING_SET_KEY = "pendingSyncProductIds"
        private const val CHUNK_SIZE = 500
        private const val TARGET_CIRCUIT_BREAKER = "product-stock-decrease"
    }

    @Async
    @EventListener
    fun onCircuitBreakerRecovered(event: CircuitBreakerStateChangedEvent) {
        // 특정 서킷브레이커만 처리
        if (event.circuitBreakerName != TARGET_CIRCUIT_BREAKER) {
            return
        }

        // 복구 이벤트만 처리 (OPEN/HALF_OPEN → CLOSED)
        val isRecovery = (event.previousState == CircuitBreakerManager.State.OPEN ||
                          event.previousState == CircuitBreakerManager.State.HALF_OPEN) &&
                         event.currentState == CircuitBreakerManager.State.CLOSED

        if (!isRecovery) {
            return
        }

        logger.info("Circuit Breaker [${event.circuitBreakerName}] recovered. Starting cache warming...")

        inventoryScope.launch {
            try {
                performCacheWarming()
            } catch (ex: Exception) {
                logger.error("Cache warming failed", ex)
            }
        }
    }

    private fun performCacheWarming() {
        val changedProductIds = fetchChangedProductIds()
        val pendingProductIds = fetchPendingProductIds()

        // 두 Set의 합집합 계산
        val allProductIds = (changedProductIds + pendingProductIds).distinct()

        if (allProductIds.isEmpty()) {
            logger.info("No products to warm cache")
            return
        }

        logger.info("Starting cache warming for ${allProductIds.size} products (changed: ${changedProductIds.size}, pending: ${pendingProductIds.size})")

        allProductIds.chunked(CHUNK_SIZE).forEachIndexed { index, productIds ->
            try {
                warmCacheChunk(productIds, index)
            } catch (ex: Exception) {
                logger.error("Failed to warm cache chunk #$index", ex)
            }
        }

        // changedProductIds는 비우고, pendingSyncProductIds는 유지 (스케줄러에서 처리)
        clearChangedProductIds(changedProductIds)

        logger.info("Cache warming completed")
    }

    private fun fetchChangedProductIds(): List<Long> {
        return try {
            val members = partitionedRedisSetRepository.members(CHANGED_SET_KEY, partitionCount)
            members.mapNotNull { it.toLongOrNull() }
        } catch (ex: Exception) {
            logger.error("Failed to fetch changed product IDs", ex)
            emptyList()
        }
    }

    private fun fetchPendingProductIds(): List<Long> {
        return try {
            val members = partitionedRedisSetRepository.members(PENDING_SET_KEY, partitionCount)
            members.mapNotNull { it.toLongOrNull() }
        } catch (ex: Exception) {
            logger.error("Failed to fetch pending product IDs", ex)
            emptyList()
        }
    }

    private fun clearChangedProductIds(productIds: List<Long>) {
        if (productIds.isEmpty()) return
        try {
            productIds.forEach { productId ->
                partitionedRedisSetRepository.remove(CHANGED_SET_KEY, productId, partitionCount)
            }
            logger.info("Cleared ${productIds.size} IDs from changedProductIds")
        } catch (ex: Exception) {
            logger.error("Failed to clear changed product IDs", ex)
        }
    }

    private fun warmCacheChunk(productIds: List<Long>, chunkIndex: Int) {
        val inventories = inventoryPersistence.findAllByProductIdIn(productIds, LocalDate.now())

        if (inventories.isEmpty()) {
            logger.warn("Chunk #$chunkIndex: No inventories found in DB")
            return
        }

        inventories.forEach { inventory ->
            try {
                inventoryCachePersistence.saveHash(
                    inventory.productId,
                    com.eatngo.inventory.dto.InventoryDto(
                        quantity = inventory.quantity,
                        stock = inventory.stock
                    )
                )
            } catch (ex: Exception) {
                logger.error("Failed to warm cache for productId: ${inventory.productId}", ex)
            }
        }

        logger.info("Chunk #$chunkIndex: Warmed ${inventories.size}/${productIds.size} products")
    }
}
