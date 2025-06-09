package com.eatngo.order.dto

import com.eatngo.order.domain.Order
import com.eatngo.order.domain.OrderItem
import java.time.LocalDateTime

data class OrderDto(
    val id: Long,
    val orderNumber: Long,
    val customerId: Long,
    val storeId: Long,
    val status: String,
    val orderItems: List<OrderItemDto>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(order: Order) =
            with(order) {
                OrderDto(
                    id = order.id,
                    orderNumber = order.orderNumber,
                    customerId = order.customerId,
                    storeId = order.storeId,
                    status = order.status.name,
                    orderItems = order.orderItems.map { OrderItemDto.from(it) },
                    createdAt = order.createdAt,
                    updatedAt = order.updatedAt,
                )
            }
    }
}

data class OrderItemDto(
    val id: Long,
    val productId: Long,
    val productName: String,
    val originPrice: Int,
    val finalPrice: Int,
    val imageUrl: String?,
    val quantity: Int,
) {
    companion object {
        fun from(orderItem: OrderItem) = with(orderItem) {
            OrderItemDto(
                id = id,
                productId = productId,
                productName = productName,
                originPrice = originPrice,
                finalPrice = finalPrice,
                imageUrl = imageUrl,
                quantity = quantity,
            )
        }
    }
}