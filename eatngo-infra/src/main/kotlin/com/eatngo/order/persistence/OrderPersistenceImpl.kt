package com.eatngo.order.persistence

import com.eatngo.common.response.Cursor
import com.eatngo.extension.mapOrNull
import com.eatngo.order.domain.Order
import com.eatngo.order.dto.OrderQueryParamDto
import com.eatngo.order.infra.OrderPersistence
import com.eatngo.order.rdb.entity.OrderJpaEntity
import com.eatngo.order.rdb.repository.OrderRdbRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
class OrderPersistenceImpl(
    val orderRdbRepository: OrderRdbRepository,
) : OrderPersistence {
    override fun save(order: Order) =
        OrderJpaEntity.toOrder(
            orderRdbRepository.save(
                OrderJpaEntity.from(order)
            )
        )

    override fun findById(id: Long): Order? =
        orderRdbRepository
            .findById(id)
            .mapOrNull(OrderJpaEntity::toOrder)

    override fun findAllByQueryParameter(
        queryParam: OrderQueryParamDto
    ): Cursor<Order> {
        val pageRequest = PageRequest.of(0, 50, Sort.by("id").descending())

        val cursoredOrderJpaEntities = orderRdbRepository.cursoredFindAllByStatus(
            status = queryParam.status,
            customerId = queryParam.customerId,
            storeId = queryParam.storeId,
            lastId = queryParam.lastId,
            pageable = pageRequest
        )

        return Cursor.from(
            cursoredOrderJpaEntities.content
                .map(OrderJpaEntity::toOrder),
            cursoredOrderJpaEntities.lastOrNull()?.id
        )
    }
}