package com.eatngo.subscription.domain

import com.eatngo.common.constant.StoreEnum
import com.eatngo.common.exception.store.StoreException
import java.time.LocalDateTime

/**
 * 매장 구독 도메인 모델
 * 사용자가 매장을 즐겨찾기(구독)하는 정보를 관리합니다.
 */
class StoreSubscription(
    val id: Long = 0,               // 구독 정보의 고유 id
    val userId: Long,               // 구독한 사용자의 계정 id
    val storeId: Long,              // 구독된 매장의 매장 id
    val createdAt: LocalDateTime = LocalDateTime.now(), // 생성일
    var updatedAt: LocalDateTime = createdAt,           // 수정일
    var deletedAt: LocalDateTime? = null,               // 삭제일(softDel용, 삭제 시간이 존재하면 softDel)
) {
    companion object {
        /**
         * 새로운 구독 생성
         */
        fun create(
            userId: Long,
            storeId: Long
        ): StoreSubscription {
            return StoreSubscription(
                id = 0,
                userId = userId,
                storeId = storeId,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        }
    }

    fun isActive(): Boolean = deletedAt == null

    fun restore() {
        if (isActive()) throw StoreException.SubscriptionAlreadyActive(id)
        deletedAt = null
        updatedAt = LocalDateTime.now()
    }

    fun softDelete() {
        if (!isActive()) throw StoreException.SubscriptionAlreadyCanceled(id)
        deletedAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
    }

    /**
     * 구독 토글/재구독 통합 메서드
     * @return 토글 후 상태
     */
    fun toggleOrRestore(): StoreEnum.SubscriptionStatus {
        return if (isActive()) {
            softDelete()
            StoreEnum.SubscriptionStatus.CANCELED
        } else {
            restore()
            StoreEnum.SubscriptionStatus.RESUMED
        }
    }

    /**
     * 사용자 소유 구독인지 확인
     * @param customerId 고객 ID
     * @throws StoreException.SubscriptionForbidden 구독 소유자가 아닌 경우
     */
    fun validateOwnership(customerId: Long) {
        if (userId != customerId) {
            throw StoreException.SubscriptionForbidden(customerId, id)
        }
    }
}