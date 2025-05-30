package com.eatngo.subscription.event

import java.time.LocalDateTime

/**
 * 구독 이벤트 기본 인터페이스
 */
sealed interface SubscriptionEvent {
    val subscriptionId: Long
    val customerId: Long
    val storeId: Long
    val timestamp: LocalDateTime
}

/**
 * 구독 생성 이벤트
 */
data class SubscriptionCreatedEvent(
    override val subscriptionId: Long,
    override val customerId: Long,
    override val storeId: Long,
    override val timestamp: LocalDateTime = LocalDateTime.now()
) : SubscriptionEvent

/**
 * 구독 취소 이벤트
 */
data class SubscriptionCanceledEvent(
    override val subscriptionId: Long,
    override val customerId: Long,
    override val storeId: Long,
    override val timestamp: LocalDateTime = LocalDateTime.now()
) : SubscriptionEvent

/**
 * 구독 재개 이벤트
 */
data class SubscriptionResumedEvent(
    override val subscriptionId: Long,
    override val customerId: Long,
    override val storeId: Long,
    override val timestamp: LocalDateTime = LocalDateTime.now()
) : SubscriptionEvent 