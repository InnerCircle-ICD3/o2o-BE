package com.eatngo.inventory.batch

import com.eatngo.inventory.event.InventoryChangedEvent
import com.eatngo.inventory.event.InventorySyncEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Service
class InventoryEventListener(
    private val reactiveStringRedisTemplate: ReactiveStringRedisTemplate,
    private val inventoryScope: CoroutineScope
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val CHANGED_SET_KEY = "changedProductIds"
        private const val PENDING_SET_KEY = "pendingSyncProductIds"
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onInventoryChangedByOrder(event: InventoryChangedEvent) {
        addChangedProductId(event.productId)
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onInventoryChangedByStoreOwner(event: InventorySyncEvent) {
        addChangedProductId(event.productId)
    }

    private fun addChangedProductId(productId: Long) {
        inventoryScope.launch {
            try {
                val changedProductId = productId.toString()
                reactiveStringRedisTemplate.opsForSet()
                    .add(CHANGED_SET_KEY, changedProductId)
                    .awaitSingle()
            } catch (ex: Exception) {
                // Redis 실패 시 pending Set에 추가 (복구 시 처리)
                logger.warn("Redis Set 추가 실패, pending으로 전환: $productId", ex)
                try {
                    reactiveStringRedisTemplate.opsForSet()
                        .add(PENDING_SET_KEY, productId.toString())
                        .awaitSingle()
                } catch (pendingEx: Exception) {
                    logger.error("Pending Set 추가도 실패: $productId", pendingEx)
                }
            }
        }
    }
}