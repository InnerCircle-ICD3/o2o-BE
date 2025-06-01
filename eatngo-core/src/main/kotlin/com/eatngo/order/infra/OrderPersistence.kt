package com.eatngo.order.infra

import com.eatngo.common.response.Cursor
import com.eatngo.order.domain.Order
import com.eatngo.order.dto.OrderQueryParamDto

interface OrderPersistence {
    fun save(order: Order): Order

    fun update(order: Order): Order

    fun findById(id: Long): Order?

    fun findAllByQueryParameter(queryParam: OrderQueryParamDto): Cursor<Order>
}