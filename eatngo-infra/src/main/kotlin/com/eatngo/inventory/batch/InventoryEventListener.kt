package com.eatngo.inventory.batch

import com.eatngo.inventory.event.InventoryChangedEvent
import com.eatngo.inventory.event.InventorySyncEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Service
class InventoryEventListener(
    private val redisTemplate: ReactiveStringRedisTemplate,
    private val inventoryScope: CoroutineScope
) {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onInventoryChangedByOrder(event: InventoryChangedEvent) {
        inventoryScope.launch {
            val changedProductId = event.productId.toString()
            redisTemplate.opsForSet()
                .add("changedProductIds", changedProductId)
                .awaitSingle()
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onInventoryChangedByStoreOwner(event: InventorySyncEvent) {
        inventoryScope.launch {
            val changedProductId = event.productId.toString()
            redisTemplate.opsForSet()
                .add("changedProductIds", changedProductId)
                .awaitSingle()
        }
    }
}