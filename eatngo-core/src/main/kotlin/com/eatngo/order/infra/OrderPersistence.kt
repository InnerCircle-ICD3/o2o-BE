package com.eatngo.order.infra

import com.eatngo.order.domain.Order

interface OrderPersistence {
    fun save(order: Order): Order

    fun findById(id: Long): Order?
}