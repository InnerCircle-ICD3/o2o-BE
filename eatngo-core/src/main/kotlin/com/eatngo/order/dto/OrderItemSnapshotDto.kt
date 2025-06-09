package com.eatngo.order.dto

data class OrderItemSnapshotDto(
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val originPrice: Int,
    val finalPrice: Int,
    val imageUrl: String?
)