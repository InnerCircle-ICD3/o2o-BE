package com.eatngo.order.usecase

import com.eatngo.common.response.Cursor
import com.eatngo.customer.service.CustomerService
import com.eatngo.order.dto.OrderDto
import com.eatngo.order.dto.OrderQueryParamDto
import com.eatngo.order.service.OrderService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomerReadOrderUseCase(
    val orderService: OrderService,
    val customerService: CustomerService
) {
    @Transactional(readOnly = true)
    fun findAllByQueryParameter(queryParam: OrderQueryParamDto): Cursor<OrderDto> {
        customerService.getCustomerById(queryParam.customerId!!)
        val cursoredOrders = orderService.findAllByQueryParam(queryParam)

        return Cursor.from(cursoredOrders.contents.map { OrderDto.from(it) }, lastId = cursoredOrders.lastId)
    }
}