package com.eatngo.inventory.event

interface InventoryEventPublisher {
    fun publishInventoryChangedEvent(inventoryChangedEvent: InventoryChangedEvent)
}