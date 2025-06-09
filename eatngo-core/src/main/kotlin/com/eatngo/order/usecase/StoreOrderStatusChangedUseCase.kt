package com.eatngo.order.usecase

import com.eatngo.order.domain.Status
import com.eatngo.order.dto.StoreOrderStatusChangedDto
import com.eatngo.order.event.OrderEvent
import com.eatngo.order.service.OrderService
import com.eatngo.store.service.impl.StoreServiceImpl
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StoreOrderStatusChangedUseCase(
    private val orderService: OrderService,
    private val storeService: StoreServiceImpl,
    private val eventPublisher: ApplicationEventPublisher
) {
    @Transactional
    fun change(dto: StoreOrderStatusChangedDto) {
        val store = storeService.getStoreById(dto.userId)

        require(store.storeOwnerId == dto.userId) { "자신의 상점만 조회할 수 있습니다." }

        val order = orderService.getById(dto.orderId)

        when (dto.status) {
            Status.CANCELED -> order.toCancel(store)
            Status.CONFIRMED -> order.toConfirm(store)
            else -> {
                throw IllegalStateException("Cannot '${dto.status}' when status is '${order.status}'")
            }
        }

        orderService.update(order)

        OrderEvent.from(order, store.id)
            ?.let { eventPublisher.publishEvent(it) }
    }
}