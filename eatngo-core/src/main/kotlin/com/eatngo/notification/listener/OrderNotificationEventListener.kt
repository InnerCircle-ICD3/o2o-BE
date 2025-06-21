package com.eatngo.notification.listener

import com.eatngo.notification.event.NotificationEvent
import com.eatngo.notification.event.NotificationEventType
import com.eatngo.notification.infra.NotificationPublisher
import com.eatngo.order.dto.OrderDto
import com.eatngo.order.event.OrderReadiedEvent
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class OrderNotificationEventListener(
    private val notificationPublisher: NotificationPublisher,
) {
    @TransactionalEventListener
    fun handle(event: OrderReadiedEvent) {
        notificationPublisher.publish(
            NotificationEvent(
                storeId = event.userId,
                eventType = NotificationEventType.ORDER_READIED,
                message = OrderDto.from(event.order),
            ),
        )
    }
}
