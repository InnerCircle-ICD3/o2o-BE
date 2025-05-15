package com.eatngo.store.dto

import com.eatngo.store.domain.NotificationType
import com.eatngo.store.domain.StoreSubscription
import java.time.LocalDateTime

/**
 * 상점 구독 DTO 클래스들
 */
object StoreSubscriptionDto {
    /**
     * 상점 구독 생성 요청 DTO
     */
    data class CreateRequest(
        val userId: String,
        val storeId: Long,
        val notificationEnabled: Boolean = true
    )
    
    /**
     * 알림 상태 변경 요청 DTO
     */
    data class NotificationUpdateRequest(
        val notificationEnabled: Boolean
    )
    
    /**
     * 상점 구독 알림 타입 설정 요청 DTO
     */
    data class NotificationTypeUpdateRequest(
        val notificationTypes: List<NotificationType>
    )
    
    /**
     * 상점 구독 응답 DTO
     */
    data class Response(
        val id: String,
        val userId: String,
        val storeId: Long,
        val notificationEnabled: Boolean,
        val createdAt: String,
        val updatedAt: String,
        val store: StoreDto.SummaryResponse? = null,
        val isActive: Boolean = true
    ) {
        companion object {
            fun from(subscription: StoreSubscription, includeStore: Boolean = false): Response {
                return Response(
                    id = subscription.id,
                    userId = subscription.userId,
                    storeId = subscription.storeId,
                    notificationEnabled = subscription.notificationEnabled,
                    createdAt = subscription.createdAt.toString(),
                    updatedAt = subscription.updatedAt.toString(),
                    isActive = subscription.deletedAt == null
                )
            }
        }
    }
    
    /**
     * 상점 구독 요약 응답 DTO (목록 조회용)
     */
    data class SummaryResponse(
        val id: String,
        val storeId: Long,
        val userId: String,
        val notificationEnabled: Boolean,
        val createdAt: String,
        val store: StoreDto.SummaryResponse? = null
    ) {
        companion object {
            fun from(subscription: StoreSubscription): SummaryResponse {
                return SummaryResponse(
                    id = subscription.id,
                    storeId = subscription.storeId,
                    userId = subscription.userId,
                    notificationEnabled = subscription.notificationEnabled,
                    createdAt = subscription.createdAt.toString()
                )
            }
        }
    }
}