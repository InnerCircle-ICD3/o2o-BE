package com.eatngo.order.dto

import com.eatngo.order.domain.Order
import com.eatngo.order.domain.OrderItem
import java.time.ZonedDateTime

data class OrderDto(
    val id: Long,
    val orderNumber: Long,
    val customerId: Long,
    val storeId: Long,
    val status: String,
    val orderItems: List<OrderItemDto>,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime,
) {
    companion object {
        fun from(order: Order) =
            with(order) {
                OrderDto(
                    id = id,
                    orderNumber = orderNumber,
                    customerId = customerId,
                    storeId = storeId,
                    status = status.name,
                    orderItems = orderItems.map { OrderItemDto.from(it) },
                    createdAt = createdAt,
                    updatedAt = updatedAt,
                )
            }
    }
}

data class OrderItemDto(
    val id: Long,
    val productId: Long,
    val productName: String,
    val price: Int,
    val quantity: Int,
) {
    companion object {
        fun from(orderItem: OrderItem) =
            with(orderItem) {
                OrderItemDto(
                    id = id,
                    productId = productId,
                    productName = productName,
                    price = price,
                    quantity = quantity,
                )
            }
    }
}