package com.eatngo.order.event

data class CreateOrderEvent(
    val orderId: Long,
    val productId: Long,
    val quantity: Int,
) {
}