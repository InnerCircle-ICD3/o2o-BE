package com.eatngo.notification.event

interface NotificationMessage

data class OrderCreatedMessage(
    val orderId : Long,
    val orderItems: List<OrderItemMessage>
) : NotificationMessage

data class OrderItemMessage(
    val name: String,
    val quantity: Int,
)

data class NotificationEvent<T: NotificationMessage>(
    val storeId: Long,
    val eventType: PushEventType,
    val message: T
)

enum class PushEventType(
    val eventName: String,
    val messageClass: Class<out NotificationMessage>
) {
    ORDER_CREATED("ORDER_CREATED", OrderCreatedMessage::class.java),
}