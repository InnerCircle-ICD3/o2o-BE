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
        fun from(order: Order): OrderDto {
            return OrderDto(
                order.id,
                order.orderNumber,
                order.customerId,
                order.storeId,
                order.status.name,
                order.orderItems.map { OrderItemDto.from(it) },
                order.createdAt,
                order.updatedAt,
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
        fun from(orderItem: OrderItem): OrderItemDto {
            return OrderItemDto(
                orderItem.id,
                orderItem.productId,
                orderItem.productName,
                orderItem.price,
                orderItem.quantity,
            )
        }
    }
}