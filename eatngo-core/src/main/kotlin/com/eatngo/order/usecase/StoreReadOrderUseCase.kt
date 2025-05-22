package com.eatngo.order.usecase

import com.eatngo.common.response.Cursor
import com.eatngo.order.dto.OrderDto
import com.eatngo.order.dto.OrderQueryParamDto
import com.eatngo.order.service.OrderService
import com.eatngo.store_owner.service.StoreOwnerService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StoreReadOrderUseCase(
    val orderService: OrderService,
    val storeService: StoreOwnerService
) {
    @Transactional(readOnly = true)
    fun findAllByQueryParameter(queryParam: OrderQueryParamDto): Cursor<OrderDto> {
        storeService.getStoreOwnerById(queryParam.storeId!!)
        val cursoredOrders = orderService.findAllByQueryParam(queryParam)

        return Cursor.from(cursoredOrders.contents.map { OrderDto.from(it) }, lastId = cursoredOrders.lastId)
    }
}