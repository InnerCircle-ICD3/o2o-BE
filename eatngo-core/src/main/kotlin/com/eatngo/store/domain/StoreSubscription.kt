package com.eatngo.store.domain

import com.eatngo.store.dto.StoreSubscriptionDto
import com.eatngo.store.dto.StoreSubscriptionSummary
import java.time.ZonedDateTime

/**
 * 매장 구독 도메인 모델
 * 사용자가 매장을 즐겨찾기(구독)하는 정보를 관리합니다.
 */
class StoreSubscription(
    val id: String = "",            // 구독 정보의 고유 id
    val userId: String,             // 구독한 사용자의 계정 id
    val storeId: Long,              // 구독된 매장의 매장 id
    val createdAt: ZonedDateTime = ZonedDateTime.now(),   // 생성일
    val updatedAt: ZonedDateTime = createdAt,   // 수정일
    var deletedAt: ZonedDateTime? = null, // 삭제일(softDel용, 삭제 시간이 존재하면 softDel)
) {
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

    /**
     * 상점 구독 응답 DTO로 변환
     */
    fun toResponseDto(subscription: StoreSubscription, store: Store? = null): StoreSubscriptionDto {
        return StoreSubscriptionDto(
            id = subscription.id,
            userId = subscription.userId,
            storeId = subscription.storeId,
            createdAt = subscription.createdAt.toString(),
            updatedAt = subscription.updatedAt.toString(),
            store = store?.toSummaryDto(),
            subscribed = subscription.deletedAt == null
        )
    }

    /**
     * 상점 구독 요약 응답 DTO로 변환
     */
    fun toSummaryDto(store: Store? = null): StoreSubscriptionSummary {
        val storeDto = store?.toSummaryDto()
        return StoreSubscriptionSummary(
            id = id,
            storeId = storeId,
            userId = userId,
            createdAt = createdAt.toString(),
            store = storeDto
        )
    }
}