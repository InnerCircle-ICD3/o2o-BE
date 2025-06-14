package com.eatngo.inventory.event

interface InventoryEventPublisher {
    fun sendInventoryChangedEvent(inventoryChangedEvent: InventoryChangedEvent)
}