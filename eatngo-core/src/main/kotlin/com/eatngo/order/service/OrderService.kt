package com.eatngo.order.service

import com.eatngo.order.domain.Order
import com.eatngo.order.domain.OrderItem
import com.eatngo.order.dto.OrderCreateDto
import com.eatngo.order.dto.OrderDto
import com.eatngo.order.infra.OrderPersistence
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderPersistence: OrderPersistence
) {
    fun createOrder(orderDto: OrderCreateDto): OrderDto {
        val order: Order = Order.create(
            orderNumber = 1L,
            customerId = orderDto.customerId,
            storeId = orderDto.storeId,
            orderItems = orderDto.orderItems.map {
                OrderItem.of(
                    it.productId,
                    it.productName,
                    it.price,
                    it.quantity
                )
            }
        )

        return OrderDto.from(orderPersistence.save(order))
    }
}