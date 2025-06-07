package com.eatngo.subscription.dto
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * 매장 구독 내용을 요약해서 반환하는 리스폰스 (구독 목록 조회용)
 */
data class StoreSubscriptionResponse(
    val id: Long,                     // 구독 ID
    val storeId: Long,                // 매장 ID
    val storeName: String,            // 매장명
    val description: String?,         // 매장 설명
    val mainImageUrl: String?,        // 매장 대표 이미지
    val foodCategory: List<String>?,        // 음식 카테고리
    val status: String,               // 매장 상태 (OPEN, CLOSED 등)
    val discountRate: Double?,         // 할인율 (10% → 0.1)
    val originalPrice: Int?,           // 원가
    val discountedPrice: Int?,         // 할인된 가격
    val todayPickupStartTime: LocalTime?, // 오늘 픽업 시작 시간
    val todayPickupEndTime: LocalTime?,   // 오늘 픽업 종료 시간
    val totalStockCount: Int?,        // 총 재고 수량
    val pickupDay: String?,           // 픽업 가능 요일
    val subscribedAt: LocalDateTime,  // 구독 일시 (정렬, 표시용)
) {
    companion object {
        fun from(dto: StoreSubscriptionDto): StoreSubscriptionResponse {
            return StoreSubscriptionResponse(
                id = dto.id,
                storeId = dto.storeId,
                storeName = dto.storeName,
                description = dto.description,
                mainImageUrl = dto.mainImageUrl,
                foodCategory = dto.foodCategory,
                status = dto.status.name,
                discountRate = dto.discountRate,
                originalPrice = dto.originalPrice,
                discountedPrice = dto.discountedPrice,
                todayPickupStartTime = dto.todayPickupStartTime,
                todayPickupEndTime = dto.todayPickupEndTime,
                totalStockCount = dto.totalStockCount,
                pickupDay = dto.pickupDay,
                subscribedAt = dto.createdAt
            )
        }
    }
}

/**
 * 매장 구독 토글 시 반환하는 리스폰스(구독 성공용)
 */
data class SubscriptionToggleResponse(
    val id: Long,                    // 구독 ID
    val userId: Long,                // 구독한 사용자의 계정 ID
    val storeId: Long,               // 매장 ID
    val subscribed: Boolean,         // 구독 여부
    val actionTime: LocalDateTime    // 구독/해제 시간
) {
    companion object {
        fun from(dto: StoreSubscriptionDto): SubscriptionToggleResponse {
            return SubscriptionToggleResponse(
                id = dto.id,
                userId = dto.userId,
                storeId = dto.storeId,
                subscribed = dto.deletedAt == null,
                actionTime = dto.updatedAt
            )
        }
    }
}