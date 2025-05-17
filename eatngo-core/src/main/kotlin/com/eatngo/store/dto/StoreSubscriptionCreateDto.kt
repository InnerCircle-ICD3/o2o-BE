package com.eatngo.store.dto

import com.eatngo.store.constant.StoreEnum
import com.eatngo.store.domain.Store
import com.eatngo.store.domain.StoreSubscription
import java.time.ZonedDateTime

/**
 * 상점 구독 생성 요청 DTO
 * 사용자가 매장을 구독하면 자동으로 알림을 받게 됩니다.
 */
data class StoreSubscriptionCreateDto(
    val userId: String,
    val storeId: Long
) {
    fun toDomain(subscriptionId: String, createdAt: ZonedDateTime = ZonedDateTime.now()): StoreSubscription {
        return StoreSubscription(
            id = subscriptionId,
            userId = userId,
            storeId = storeId,
            createdAt = createdAt,
            updatedAt = createdAt
        )
    }
}