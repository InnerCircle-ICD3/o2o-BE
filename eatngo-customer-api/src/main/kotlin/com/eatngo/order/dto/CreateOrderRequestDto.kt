package com.eatngo.order.dto

data class CreateOrderRequestDto(
    val orderItems: List<CreateOrderItemRequestDto>,
    val storeId: Long,
)
data class CreateOrderItemRequestDto(
    val productId: Long,
    val productName: String,
    val price: Int,
    val quantity: Int,
)