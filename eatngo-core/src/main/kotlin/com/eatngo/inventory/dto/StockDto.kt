package com.eatngo.inventory.dto

data class StockDto(
    val orderId: Long,
    val productId: Long,
    val quantity: Int
) {
}