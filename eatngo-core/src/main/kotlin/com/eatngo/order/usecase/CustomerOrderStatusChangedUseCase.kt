package com.eatngo.order.usecase

import com.eatngo.customer.service.CustomerService
import com.eatngo.order.domain.Status
import com.eatngo.order.dto.OrderStatusChangedDto
import com.eatngo.order.event.OrderEvent
import com.eatngo.order.service.OrderService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomerOrderStatusChangedUseCase(
    private val orderService: OrderService,
    private val customerService: CustomerService,
    private val eventPublisher: ApplicationEventPublisher
) {
    @Transactional
    fun change(dto: OrderStatusChangedDto) {
        val customer = customerService.getCustomerById(dto.userId)
        val order = orderService.getById(dto.orderId)

        when (dto.status) {
            Status.READY -> order.toReady(customer)
            Status.CANCELED -> order.toCancel(customer)
            Status.DONE -> order.toDone(customer)
            else -> {
                throw IllegalStateException("Cannot '${dto.status}' when status is '${order.status}'")
            }
        }

        orderService.update(order)

        OrderEvent.from(order, customer.id)
            ?.let { eventPublisher.publishEvent(it) }
    }
}