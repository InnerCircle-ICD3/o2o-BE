package com.eatngo.inventory.event

interface InventoryEventPublisher {
    fun publishInventoryChangedEvent(
        productId: Long,
        quantity: Int,
        stock: Int
    )
}