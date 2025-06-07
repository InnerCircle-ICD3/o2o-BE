package com.eatngo.subscription.dto

import com.eatngo.common.constant.StoreEnum
import com.eatngo.subscription.domain.StoreSubscription
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * 상점 구독 DTO
 */
data class StoreSubscriptionDto(
    val id: Long,
    val userId: Long,
    val storeId: Long,
    val storeName: String,            // 매장명
    val description: String? = null,
    val mainImageUrl: String?,        // 매장 대표 이미지
    val foodCategory: List<String>? = null,
    val status: StoreEnum.StoreStatus,        // 매장 상태 (OPEN, CLOSED 등)
    val discountRate: Double? = null,         // 할인율 (10% → 0.1)
    val originalPrice: Int? = null,           // 원가
    val discountedPrice: Int? = null,         // 할인된 가격
    val todayPickupStartTime: LocalTime? = null,
    val todayPickupEndTime: LocalTime? = null,
    val totalStockCount: Int? = null,
    val pickupDay: String?? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val deletedAt: LocalDateTime? = null,
) {
    /**
     * 구독 활성 상태 여부
     */
    fun isActive(): Boolean = deletedAt == null


    companion object {
        /**
         * 전체 정보를 포함한 DTO 생성
         */
        fun from(
            subscription: StoreSubscription,
            storeName: String,
            description: String?,
            mainImageUrl: String?,
            foodCategory: List<String>?,
            status: StoreEnum.StoreStatus,
            discountRate: Double,
            originalPrice: Int,
            discountedPrice: Int,
            todayPickupStartTime: LocalTime?,
            todayPickupEndTime: LocalTime?,
            totalStockCount: Int?,
            pickupDay: String?,
        ): StoreSubscriptionDto {
            return StoreSubscriptionDto(
                id = subscription.id,
                userId = subscription.userId,
                storeId = subscription.storeId,
                storeName = storeName,
                description = description,
                mainImageUrl = mainImageUrl,
                foodCategory = foodCategory,
                status = status,
                discountRate = discountRate,
                originalPrice = originalPrice,
                discountedPrice = discountedPrice,
                todayPickupStartTime = todayPickupStartTime,
                todayPickupEndTime = todayPickupEndTime,
                totalStockCount = totalStockCount,
                pickupDay = pickupDay,
                createdAt = subscription.createdAt,
                updatedAt = subscription.updatedAt,
                deletedAt = subscription.deletedAt
            )
        }

        /**
         * 기본 정보만 포함한 DTO 생성 (할인 정보 제외)
         */
        fun from(
            subscription: StoreSubscription,
            storeName: String,
            mainImageUrl: String?,
            status: StoreEnum.StoreStatus
        ): StoreSubscriptionDto {
            return StoreSubscriptionDto(
                id = subscription.id,
                userId = subscription.userId,
                storeId = subscription.storeId,
                storeName = storeName,
                mainImageUrl = mainImageUrl,
                status = status,
                createdAt = subscription.createdAt,
                updatedAt = subscription.updatedAt,
                deletedAt = subscription.deletedAt
            )
        }
    }
}