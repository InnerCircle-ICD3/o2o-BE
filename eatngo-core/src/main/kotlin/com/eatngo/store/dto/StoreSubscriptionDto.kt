package com.eatngo.store.dto

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.domain.StoreSubscription
import java.time.LocalDateTime

/**
 * 상점 구독 DTO
 */
data class StoreSubscriptionDto(
    val id: Long,
    val userId: Long,
    val userName: String,
    val storeId: Long,
    val storeName: String,            // 매장명
    val mainImageUrl: String?,        // 매장 대표 이미지
    val status: StoreEnum.StoreStatus,               // 매장 상태 (OPEN, CLOSED 등)
    val discountRate: Double,         // 할인율 (10% → 0.1)
    val originalPrice: Int,           // 원가
    val discountedPrice: Int,         // 할인된 가격
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val deletedAt: LocalDateTime? = null,
){
    companion object {
        fun from(
            subscription: StoreSubscription,
            userName: String,
            storeName: String,
            mainImageUrl: String?,
            status: StoreEnum.StoreStatus,
            discountRate: Double,
            originalPrice: Int,
            discountedPrice: Int
        ): StoreSubscriptionDto {
            return StoreSubscriptionDto(
                id = subscription.id,
                userId = subscription.userId,
                userName = userName,
                storeId = subscription.storeId,
                storeName = storeName,
                mainImageUrl = mainImageUrl,
                status = status,
                discountRate = discountRate,
                originalPrice = originalPrice,
                discountedPrice = discountedPrice,
                createdAt = subscription.createdAt,
                updatedAt = subscription.updatedAt,
                deletedAt = subscription.deletedAt
            )
        }
    }
}