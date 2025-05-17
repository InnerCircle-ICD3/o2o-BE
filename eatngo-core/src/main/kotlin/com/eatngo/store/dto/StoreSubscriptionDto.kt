package com.eatngo.store.dto

import com.eatngo.store.domain.Store
import com.eatngo.store.domain.StoreSubscription

/**
 * 상점 구독 DTO
 */
data class StoreSubscriptionDto(
    val id: String,
    val userId: String,
    val storeId: Long,
    val createdAt: String,
    val updatedAt: String,
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
    val id: String,
    val storeId: Long,
    val userId: String,
    val createdAt: String,
    val store: StoreSummary? = null
) {
    companion object {
        fun from(subscription: StoreSubscription, store: Store? = null): StoreSubscriptionSummary {
            return subscription.toSummaryDto(store)
        }
    }
}