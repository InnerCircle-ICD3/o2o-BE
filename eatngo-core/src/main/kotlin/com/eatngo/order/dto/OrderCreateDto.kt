package com.eatngo.order.dto


data class OrderCreateDto(
    val customerId: Long,
    val storeId: Long,
    val orderItems: List<OrderItemCreateDto>,
)

data class OrderItemCreateDto(
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val price: Int
)
