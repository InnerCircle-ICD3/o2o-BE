package com.eatngo.notification.event

import com.eatngo.order.dto.OrderDto

data class NotificationEvent<T>(
    val storeId: Long,
    val eventType: NotificationEventType,
    val message: T,
)

enum class NotificationEventType(
    val eventName: String,
    val messageClass: Class<*>,
) {
    ORDER_READIED("ORDER_READIED", OrderDto::class.java),
    HEARTBEAT("HEARTBEAT", EmptyMessage::class.java),
    CONNECTED("CONNECTED", EmptyMessage::class.java),
}

class EmptyMessage
