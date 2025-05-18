package com.eatngo.order.domain

import java.time.LocalDateTime

class Order(
    val id: Long = 0,
    val orderNumber: Long,
    val orderItems: List<OrderItem>,
    val customerId: Long,
    val storeId: Long,
    val status: Status,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun create(customerId: Long, storeId: Long, orderNumber: Long, orderItems: List<OrderItem>): Order {
            return Order(
                0,
                orderNumber,
                orderItems,
                customerId,
                storeId,
                Status.CREATED,
                LocalDateTime.now(),
                LocalDateTime.now()
            )
        }
    }
}