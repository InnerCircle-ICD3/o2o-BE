package com.eatngo.order.event

interface PushMessage

data class OrderCreatedMessage(
    val orderId : Long,
    val orderItems: List<OrderItemMessage>
) : PushMessage

data class OrderItemMessage(
    val name: String,
    val quantity: Int,
)

data class PushEvent<T: PushMessage>(
    val storeId: Long,
    val eventType: PushEventType,
    val message: T
)

enum class PushEventType(
    val eventName: String,
    val messageClass: Class<out PushMessage>
) {
    ORDER_CREATED("ORDER_CREATED", OrderCreatedMessage::class.java),
}