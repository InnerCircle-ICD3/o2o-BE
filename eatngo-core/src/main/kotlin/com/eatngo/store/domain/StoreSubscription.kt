package com.eatngo.store.domain

import java.time.ZonedDateTime

/**
 * 매장 구독 도메인 모델
 * 사용자가 매장을 즐겨찾기(구독)하는 정보를 관리합니다.
 */
class StoreSubscription(
    val id: String,
    val userId: String,
    val storeId: Long,
    val notificationEnabled: Boolean, // 알림 활성화 여부
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime,
    var deletedAt: ZonedDateTime? = null,
) {
    /**
     * 알림 활성화
     */
    fun enableNotifications(): StoreSubscription {
        return if (notificationEnabled) {
            this
        } else {
            StoreSubscription(
                id = this.id,
                userId = this.userId,
                storeId = this.storeId,
                notificationEnabled = true,
                createdAt = this.createdAt,
                updatedAt = ZonedDateTime.now(),
                deletedAt = this.deletedAt
            )
        }
    }
    
    /**
     * 알림 비활성화
     */
    fun disableNotifications(): StoreSubscription {
        return if (!notificationEnabled) {
            this
        } else {
            StoreSubscription(
                id = this.id,
                userId = this.userId,
                storeId = this.storeId,
                notificationEnabled = false,
                createdAt = this.createdAt,
                updatedAt = ZonedDateTime.now(),
                deletedAt = this.deletedAt
            )
        }
    }
    
    /**
     * Soft Delete를 위한 메서드
     */
    fun softDelete(): StoreSubscription {
        val now = ZonedDateTime.now()
        this.deletedAt = now
        return this
    }
    
    /**
     * 활성화된 구독인지 확인 (삭제되지 않은 경우)
     */
    fun isActive(): Boolean {
        return deletedAt == null
    }
}

/**
 * 알림 유형
 */
enum class NotificationType {
    NEW_PRODUCT,         // 새 상품 등록 알림
    DISCOUNT,            // 할인 알림
    RESERVATION_UPDATE,  // 예약 상태 변경 알림
    STORE_UPDATE         // 매장 정보 변경 알림
} 