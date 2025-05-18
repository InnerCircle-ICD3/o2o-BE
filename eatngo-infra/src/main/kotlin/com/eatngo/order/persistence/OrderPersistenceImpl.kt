package com.eatngo.order.persistence

import com.eatngo.order.domain.Order
import com.eatngo.order.infra.OrderPersistence
import com.eatngo.order.rdb.entity.OrderJpaEntity
import com.eatngo.order.rdb.repository.OrderRdbRepository
import org.springframework.stereotype.Component

@Component
class OrderPersistenceImpl(
    val orderRdbRepository: OrderRdbRepository
) : OrderPersistence {
    override fun save(order: Order) =
        OrderJpaEntity.toOrder(
            orderRdbRepository.save(
                OrderJpaEntity.from(order)
            )
        )

}