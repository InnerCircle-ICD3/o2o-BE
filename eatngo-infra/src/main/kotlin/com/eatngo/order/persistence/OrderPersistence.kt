package com.eatngo.order.persistence

import com.eatngo.order.domain.Order
import com.eatngo.order.domain.OrderItem
import com.eatngo.order.infra.OrderPersistence
import org.springframework.stereotype.Component

@Component
class OrderPersistenceImpl: OrderPersistence {
    override fun save(order: Order): Order {
        return Order(
            id = 1L,
            orderNumber = 1L,
            orderItems = order.orderItems.mapIndexed { index, orderItem ->
                OrderItem(
                    id = (index + 1).toLong(),
                    productId = orderItem.productId,
                    productName = orderItem.productName,
                    quantity = orderItem.quantity,
                    price = orderItem.price,
                )
            },
            customerId = order.customerId,
            status = order.status,
            storeId = order.storeId,
            createdAt = order.createdAt,
            updatedAt = order.updatedAt,
        )
    }
}