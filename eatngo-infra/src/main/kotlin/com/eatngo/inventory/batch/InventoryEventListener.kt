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
                    .add("changedProductIds", changedProductId)
                    .awaitSingle()
            } catch (ex: Exception) {
                logger.error("Redis에 변경된 상품 ID 추가 실패: $productId", ex)
            }
        }
    }
}