package com.eatngo.notification.listener

import com.eatngo.notification.event.NotificationEvent
import com.eatngo.notification.event.NotificationEventType
import com.eatngo.notification.event.OrderCreatedMessage
import com.eatngo.notification.event.OrderItemMessage
import com.eatngo.notification.infra.NotificationPublisher
import com.eatngo.order.event.OrderCreatedEvent
import com.eatngo.order.event.OrderEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class OrderNotificationEventListener(
    private val notificationPublisher: NotificationPublisher
) {
    @EventListener
    fun handle(event: OrderEvent) {
        when (event) {
            is OrderCreatedEvent -> {
                for (orderItem in event.order.orderItems) {
                    notificationPublisher.publish(
                        NotificationEvent(
                            storeId = event.userId,
                            eventType = NotificationEventType.ORDER_CREATED,
                            message = OrderCreatedMessage(
                                orderId = event.order.id,
                                orderItems = event.order.orderItems.map {
                                    OrderItemMessage(it.productName, it.quantity)
                                }
                            )
                        )
                    )
                }
            }

            else -> Unit
        }
    }
}