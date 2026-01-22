package com.eatngo.inventory.batch

import com.eatngo.inventory.domain.Inventory
import com.eatngo.inventory.infra.InventoryCachePersistence
import com.eatngo.inventory.infra.InventoryPersistence
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
class InventorySyncScheduler(
    private val inventoryPersistence: InventoryPersistence,
    @Qualifier("stringRedisTemplate")
    private val stringRedisTemplate: RedisTemplate<String, String>,
    private val inventoryCachePersistence: InventoryCachePersistence,
    private val inventorySyncHealthCheck: InventorySyncHealthCheck
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val REDIS_KEY_PREFIX = "inventory:hash:"
        private const val CHANGED_SET_KEY = "changedProductIds"
        private const val PENDING_SET_KEY = "pendingSyncProductIds"
        private const val CHUNK_SIZE = 500

        private const val LUA_POP_AND_DELETE = """
            local members = redis.call('SMEMBERS', KEYS[1])
            redis.call('DEL', KEYS[1])
            return members
        """
    }

    @Scheduled(cron = "0 0 12 * * ?")
    @Transactional
    fun syncInventoryFromRedisToRdb() {
        // 1. 기존 changedProductIds 처리
        syncFromChangedIds()

        // 2. Pending 처리 (Redis UP인 경우에만)
        if (inventorySyncHealthCheck.isRedisAvailable()) {
            syncFromPendingIds()
        }
    }

    private fun syncFromChangedIds() {
        val changedIds: List<String> = fetchAndClearChangedIdsAtomically()

        if (changedIds.isEmpty()) {
            logger.info("동기화할 변경된 재고 ID가 없습니다.")
            return
        }

        val productIds = changedIds.mapNotNull { productId ->
            productId.toLongOrNull().also {
                if (it == null) {
                    logger.warn("잘못된 productId 형식: '$productId' (Long 변환 실패)")
                }
            }
        }

        if (productIds.isEmpty()) {
            logger.info("유효한 productId가 없어 동기화 대상이 없습니다.")
            return
        }

        logger.info("동기화 대상 상품 ID 개수: ${productIds.size}")

        productIds.chunked(CHUNK_SIZE).forEachIndexed { chunkIndex, idChunk ->
            try {
                updateRdbWithSingleChunk(idChunk, chunkIndex)
            } catch (ex: Exception) {
                logger.error("chunk #$chunkIndex 처리 중 오류 발생. IDs=$idChunk", ex)
            }
        }

        logger.info("Redis → RDB 재고 동기화 작업 완료.")
    }

    private fun fetchAndClearChangedIdsAtomically(): List<String> {
        val redisScript = DefaultRedisScript<List<String>>().apply {
            setScriptText(LUA_POP_AND_DELETE.trimIndent())
            resultType = List::class.java as Class<List<String>>
        }

        return stringRedisTemplate.execute(
            redisScript,
            listOf(CHANGED_SET_KEY)
        )
    }

    private fun updateRdbWithSingleChunk(
        idChunk: List<Long>,
        chunkIndex: Int
    ) {
        val inventories: MutableList<Inventory> =
            inventoryPersistence.findAllByProductIdIn(idChunk, LocalDate.now()).toMutableList()

        if (inventories.isEmpty()) {
            logger.warn("chunk #$chunkIndex: RDB에서 조회된 엔티티가 없습니다. IDs=$idChunk")
            return
        }

        inventories.forEach { inventory ->
            val redisKey = "$REDIS_KEY_PREFIX${inventory.productId}"
            val redisStock = stringRedisTemplate.opsForHash<String, String>()
                .get(redisKey, "stock")

            if (redisStock == null) {
                logger.warn("chunk #$chunkIndex: Redis 키 '$redisKey' 의 'stock' 필드가 없거나 파싱 실패. 건너뜁니다.")
                return@forEach
            }

            inventory.modify(stock = redisStock.toInt())
        }

        inventoryPersistence.saveAll(inventories)
        logger.info("chunk #$chunkIndex: ${inventories.size}건 재고 업데이트 완료.")
    }

    private fun syncFromPendingIds() {
        val pendingIds = fetchPendingIds()

        if (pendingIds.isEmpty()) {
            return
        }

        logger.info("Pending 동기화 대상 상품 ID 개수: ${pendingIds.size}")

        val productIds = pendingIds.mapNotNull { it.toLongOrNull() }
        if (productIds.isEmpty()) {
            logger.warn("Pending IDs에 유효한 productId가 없습니다.")
            return
        }

        // DB의 최신 재고를 Redis에 동기화
        productIds.chunked(CHUNK_SIZE).forEach { idChunk ->
            try {
                syncToRedis(idChunk)
            } catch (ex: Exception) {
                logger.error("Pending 동기화 중 오류: IDs=$idChunk", ex)
            }
        }

        // 성공적으로 처리한 ID는 pending Set에서 제거
        clearPendingIds(pendingIds)
    }

    private fun fetchPendingIds(): List<String> {
        return try {
            stringRedisTemplate.opsForSet().members(PENDING_SET_KEY)?.toList() ?: emptyList()
        } catch (ex: Exception) {
            logger.error("Pending IDs 조회 실패", ex)
            emptyList()
        }
    }

    private fun clearPendingIds(ids: List<String>) {
        try {
            stringRedisTemplate.opsForSet().remove(PENDING_SET_KEY, *ids.toTypedArray())
            logger.info("Pending IDs ${ids.size}건 제거 완료.")
        } catch (ex: Exception) {
            logger.error("Pending IDs 제거 실패", ex)
        }
    }

    private fun syncToRedis(productIds: List<Long>) {
        val inventories = inventoryPersistence.findAllByProductIdIn(productIds, LocalDate.now())
        if (inventories.isEmpty()) {
            logger.warn("DB에서 조회된 재고가 없습니다. IDs=$productIds")
            return
        }

        inventories.forEach { inventory ->
            inventoryCachePersistence.saveHash(
                inventory.productId,
                com.eatngo.inventory.dto.InventoryDto(quantity = inventory.quantity, stock = inventory.stock)
            )
        }
        logger.info("Pending 동기화: ${inventories.size}건 Redis 업데이트 완료.")
    }
}
