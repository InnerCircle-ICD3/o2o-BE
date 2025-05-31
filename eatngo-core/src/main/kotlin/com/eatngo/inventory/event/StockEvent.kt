package com.eatngo.inventory.event

data class StockEvent(
    val orderId: Long,
    val productId: Long,
    val quantity: Int
) {
}