package com.eatngo.order.persistence

import com.eatngo.order.domain.Order
import com.eatngo.order.infra.OrderPersistence
import org.springframework.stereotype.Component

@Component
class OrderPersistenceImpl: OrderPersistence {
    override fun save(order: Order): Order {
        return order
    }
}