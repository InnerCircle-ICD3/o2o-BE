package com.eatngo.order.usecase

import com.eatngo.order.dto.OrderCreateDto
import com.eatngo.order.dto.OrderDto
import com.eatngo.order.service.OrderService
import org.springframework.stereotype.Service

@Service
class OrderCreateUseCase(
    val orderService: OrderService
) {
    fun create(orderDto: OrderCreateDto) = OrderDto.from(orderService.createOrder(orderDto))
}