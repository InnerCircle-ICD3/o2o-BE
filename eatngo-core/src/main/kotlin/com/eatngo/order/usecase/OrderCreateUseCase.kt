package com.eatngo.order.usecase

import com.eatngo.order.dto.OrderCreateDto
import com.eatngo.order.dto.OrderDto
import com.eatngo.order.event.OrderEvent
import com.eatngo.order.service.OrderService
import jakarta.transaction.Transactional
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class OrderCreateUseCase(
    val orderService: OrderService,
    val eventPublisher: ApplicationEventPublisher
) {
    @Transactional
    fun create(orderDto: OrderCreateDto): OrderDto {
        val order = orderService.createOrder(orderDto)

        OrderEvent.from(order, order.customerId)
            ?.let { eventPublisher.publishEvent(it) }

        return OrderDto.from(order)
    }
}