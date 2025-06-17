package com.eatngo.order.usecase

import com.eatngo.order.domain.Order.Companion.AUTO_ORDER_CANCELED_MINUTE
import com.eatngo.order.domain.Status.CREATED
import com.eatngo.order.dto.OrderQueryParamDto
import com.eatngo.order.event.OrderEvent
import com.eatngo.order.service.OrderService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class OldOrderCancelUseCase(
    private val orderService: OrderService,
    private val eventPublisher: ApplicationEventPublisher,
) {
    @Transactional
    fun execute(lastId: Long?): Long? {
        val now = LocalDateTime.now()

        val cursoredOrders =
            orderService
                .findAllByQueryParam(
                    OrderQueryParamDto(
                        storeId = null,
                        customerId = null,
                        status = CREATED,
                        lastId = lastId,
                        updatedAt =
                            LocalDateTime
                                .now()
                                .minusMinutes(AUTO_ORDER_CANCELED_MINUTE),
                    ),
                )

        cursoredOrders
            .contents
            .asSequence()
            .forEach {
                it.toCancelByTimeout(now)
                orderService.update(it)
                OrderEvent
                    .from(it, it.customerId)
                    ?.let { eventPublisher.publishEvent(it) }
            }

        return cursoredOrders.lastId
    }
}
