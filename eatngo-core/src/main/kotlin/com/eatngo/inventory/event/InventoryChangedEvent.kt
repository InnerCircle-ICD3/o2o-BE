package com.eatngo.inventory.event

data class InventoryChangedEvent(
    val productId: Long,
    val quantity: Int,
    val stock: Int,
)