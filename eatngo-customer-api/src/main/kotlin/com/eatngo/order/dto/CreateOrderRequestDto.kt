package com.eatngo.order.dto

import java.time.LocalDateTime

data class CreateOrderRequestDto(
    val orderItems: List<CreateOrderItemRequestDto>,
    val storeId: Long,
    val pickupDateTime: LocalDateTime,
)
data class CreateOrderItemRequestDto(
    val productId: Long,
    val productName: String,
    val price: Int,
    val quantity: Int,
)