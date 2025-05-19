package com.eatngo.order.service

import com.eatngo.common.exception.OrderException
import com.eatngo.extension.orThrow
import com.eatngo.order.domain.Order
import com.eatngo.order.domain.OrderItem
import com.eatngo.order.dto.OrderCreateDto
import com.eatngo.order.dto.OrderDto
import com.eatngo.order.infra.OrderPersistence
import com.github.f4b6a3.tsid.TsidCreator
import org.springframework.stereotype.Service


@Service
class OrderService(
    private val orderPersistence: OrderPersistence
) {
    fun createOrder(orderDto: OrderCreateDto): OrderDto {
        val order: Order = Order.create(
            orderNumber = TsidCreator.getTsid().toLong(),
            customerId = orderDto.customerId,
            storeId = orderDto.storeId,
            orderItems = orderDto.orderItems.map {
                OrderItem.of(
                    productId = it.productId,
                    name = it.productName,
                    quantity = it.quantity,
                    price = it.price
                )
            }
        )

        return OrderDto.from(orderPersistence.save(order))
    }

    fun findById(orderId: Long): Order {
        return orderPersistence.findById(orderId).orThrow { OrderException.OrderNotFound(orderId) }
    }

    fun update(order: Order): Order {
        return orderPersistence.save(order)
    }
}