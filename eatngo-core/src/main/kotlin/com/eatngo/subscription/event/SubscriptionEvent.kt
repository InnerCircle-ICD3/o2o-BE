package com.eatngo.subscription.event

import java.time.LocalDateTime

data class SubscriptionEvent(
    val subscriptionId: Long,
    val customerId: Long,
    val storeId: Long,
    val status: SubscriptionEventStatus,
    val timestamp: LocalDateTime = LocalDateTime.now()
)


enum class SubscriptionEventStatus {
    CREATED,
    CANCELED,
    RESUMED
}
