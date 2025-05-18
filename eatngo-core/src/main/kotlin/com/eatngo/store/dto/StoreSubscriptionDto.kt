package com.eatngo.store.dto

import com.eatngo.store.constant.StoreEnum
import com.eatngo.store.domain.Store
import com.eatngo.store.domain.StoreSubscription
import java.time.LocalDateTime

/**
 * 상점 구독 DTO
 */
data class StoreSubscriptionDto(
    val id: Long,
    val userId: String,
    val storeId: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val deletedAt: LocalDateTime? = null,
    val store: StoreSummary? = null,
    val subscribed: Boolean = true
) {
    companion object {
        fun from(subscription: StoreSubscription, store: Store? = null): StoreSubscriptionDto {
            return subscription.toResponseDto(subscription, store)
        }
    }
}

/**
 * 상점 구독 요약 응답 DTO (목록 조회용)
 */
data class StoreSubscriptionSummary(
    val id: Long,
    val storeId: Long,
    val name: String,
    val mainImageUrl: String?,
    val status: StoreEnum.StoreStatus,
    val isAvailableForPickup: Boolean, // 현재 픽업 가능한지 여부
    val pickupAvailableForTomorrow: Boolean, // 내일 픽업 가능 여부
    val distance: Double? = null
) {
    companion object {
        fun from(subscription: StoreSubscription, store: Store? = null): StoreSubscriptionSummary {
            return subscription.toSummaryDto(store)
        }
    }
}