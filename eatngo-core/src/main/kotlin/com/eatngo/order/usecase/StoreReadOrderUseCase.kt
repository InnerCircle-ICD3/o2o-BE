package com.eatngo.order.usecase

import com.eatngo.common.response.Cursor
import com.eatngo.order.dto.OrderDto
import com.eatngo.order.dto.StoreOrderQueryParamDto
import com.eatngo.order.service.OrderService
import com.eatngo.store.service.impl.StoreServiceImpl
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StoreReadOrderUseCase(
    private val orderService: OrderService,
    private val storeService: StoreServiceImpl
) {
    @Transactional(readOnly = true)
    fun findAllByQueryParameter(queryParam: StoreOrderQueryParamDto): Cursor<OrderDto> {
        val store = storeService.getStoreById(queryParam.storeId)

        require(store.storeOwnerId == queryParam.storeOwnerId) { "자신의 상점만 조회할 수 있습니다." }

        val cursoredOrders = orderService.findAllByQueryParam(queryParam)

        return Cursor.from(
            content = cursoredOrders.contents
                .map { OrderDto.from(it) },
            lastId = cursoredOrders.lastId
        )
    }
}