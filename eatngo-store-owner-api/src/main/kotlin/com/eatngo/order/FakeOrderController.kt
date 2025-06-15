package com.eatngo.order

import com.eatngo.order.event.OrderCanceledEvent
import com.eatngo.order.event.OrderReadyEvent
import com.eatngo.order.service.OrderService
import com.eatngo.product.event.ProductEventListener
import com.eatngo.store.event.StorePickupEndedEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/fake-order")
@Transactional
class FakeOrderController(
    private val eventPublisher: ApplicationEventPublisher,
    private val orderService: OrderService,
    private val productEventListener: ProductEventListener,
) {

    @GetMapping("/ready/orderId/{orderId}/userId/{userId}")
    fun publishReadyEvent(@PathVariable orderId: Long, @PathVariable userId: Long) {
        val order = orderService.getById(orderId)
         productEventListener.handleOrderEvent(OrderReadyEvent(userId, order))
    }

    @GetMapping("/cancel/orderId/{orderId}/userId/{userId}")
    fun publishCancelEvent(@PathVariable orderId: Long, @PathVariable userId: Long) {
        val order = orderService.getById(orderId)
        productEventListener.handleOrderEvent(OrderCanceledEvent(userId, order))
    }

    @GetMapping("/store")
    fun createInventoryEvent() {
        eventPublisher.publishEvent(
            StorePickupEndedEvent(
                closedStoreIds = listOf(1, 2, 3),
                closedAt = LocalDateTime.now(),
            )
        )
    }
}