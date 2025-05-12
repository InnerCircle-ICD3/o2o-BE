package com.eatngo.order.domain

import java.time.ZonedDateTime

class Order(
    val id: Long = 0,
    val orderNumber: Long,
    val orderItems: List<OrderItem>,
    val customerId: Long,
    val storeId: Long,
    val status: Status,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime,
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
                ZonedDateTime.now(),
                ZonedDateTime.now()
            )
        }
    }
}