package com.eatngo.subscription.event

import com.eatngo.common.constant.StoreEnum
import java.time.LocalDateTime

data class SubscriptionEvent(
    val subscriptionId: Long,
    val customerId: Long,
    val storeId: Long,
    val status: StoreEnum.SubscriptionStatus,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
