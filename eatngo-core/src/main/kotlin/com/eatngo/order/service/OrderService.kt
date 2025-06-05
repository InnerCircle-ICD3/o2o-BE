package com.eatngo.order.service

import com.eatngo.common.exception.order.OrderException
import com.eatngo.common.response.Cursor
import com.eatngo.extension.orThrow
import com.eatngo.order.domain.Order
import com.eatngo.order.domain.OrderItem
import com.eatngo.order.dto.OrderCreateDto
import com.eatngo.order.dto.OrderQueryParamDto
import com.eatngo.order.infra.OrderPersistence
import com.github.f4b6a3.tsid.TsidCreator
import org.springframework.stereotype.Service


@Service
class OrderService(
    private val orderPersistence: OrderPersistence
) {
    fun createOrder(orderDto: OrderCreateDto): Order {
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

        return orderPersistence.save(order)
    }

    fun getById(orderId: Long): Order {
        return orderPersistence.findById(orderId).orThrow { OrderException.OrderNotFound(orderId) }
    }

    fun update(order: Order): Order {
        return orderPersistence.update(order)
    }

    fun findAllByQueryParam(queryParam: OrderQueryParamDto): Cursor<Order> =
        orderPersistence.findAllByQueryParameter(queryParam)
}