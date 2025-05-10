package com.eatngo.order.domain

import java.time.ZonedDateTime

class Order(
    val id: Long,
    val orderNumber: Long,
    val orderItems: List<OrderItem>,
    val customerId: Long,
    val marketId: Long,
    val status: Status,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime,
) {

}