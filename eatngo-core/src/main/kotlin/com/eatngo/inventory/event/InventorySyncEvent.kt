package com.eatngo.inventory.event

data class InventorySyncEvent(
    val productId: Long,
    val syncedStock: Int,
) {
}