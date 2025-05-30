package com.eatngo.store.dto
import com.eatngo.subscription.dto.StoreSubscriptionDto
import java.time.LocalDateTime

/**
 * 매장 구독 내용을 요약해서 반환하는 리스폰스 (구독 목록 조회용)
 */
data class StoreSubscriptionResponse(
    val id: Long,                     // 구독 ID
    val storeId: Long,                // 매장 ID
    val storeName: String,            // 매장명
    val mainImageUrl: String?,        // 매장 대표 이미지
    val status: String,               // 매장 상태 (OPEN, CLOSED 등)
    val discountRate: Double,         // 할인율 (10% → 0.1)
    val originalPrice: Int,           // 원가
    val discountedPrice: Int,         // 할인된 가격
    val subscribedAt: LocalDateTime,  // 구독 일시 (정렬, 표시용)
) {
    companion object {
        fun from(dto: StoreSubscriptionDto): StoreSubscriptionResponse {
            return StoreSubscriptionResponse(
                id = dto.id,
                storeId = dto.storeId,
                storeName = dto.storeName,
                mainImageUrl = dto.mainImageUrl,
                status = dto.status.name,
                discountRate = dto.discountRate,
                originalPrice = dto.originalPrice,
                discountedPrice = dto.discountedPrice,
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