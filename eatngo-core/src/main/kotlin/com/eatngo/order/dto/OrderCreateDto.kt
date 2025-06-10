package com.eatngo.order.dto

import java.time.LocalDateTime


data class OrderCreateDto(
    val customerId: Long,
    val storeId: Long,
    val pickupDateTime: LocalDateTime,
    val orderItems: List<OrderItemCreateDto>,
)

data class OrderItemCreateDto(
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val price: Int
)
