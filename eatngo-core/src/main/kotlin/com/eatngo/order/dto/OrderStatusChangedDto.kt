package com.eatngo.order.dto

import com.eatngo.order.domain.Status

data class OrderStatusChangedDto(
    val orderId: Long,
    val userId: Long,
    val status: Status
)

data class StoreOrderStatusChangedDto(
    val orderId: Long,
    val userId: Long,
    val status: Status,
    val storeId: Long
)