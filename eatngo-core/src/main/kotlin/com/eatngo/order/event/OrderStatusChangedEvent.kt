package com.eatngo.order.event

import com.eatngo.order.domain.Order
import com.eatngo.order.domain.Status

interface OrderEvent {
    companion object {
        fun from(
            order: Order,
            userId: Long,
        ): OrderEvent? =
            when (order.status) {
                Status.CREATED ->
                    OrderCreatedEvent(
                        order = order,
                        userId = userId,
                    )
                Status.READY -> null
                Status.CONFIRMED -> null
                Status.CANCELED ->
                    OrderCanceledEvent(
                        order = order,
                        userId = userId,
                    )
                Status.DONE -> null
            }
    }
}

data class OrderCreatedEvent(
    val userId: Long,
    val order: Order,
) : OrderEvent

data class OrderReadiedEvent(
    val userId: Long,
    val order: Order,
) : OrderEvent

data class OrderCanceledEvent(
    val userId: Long,
    val order: Order,
) : OrderEvent
