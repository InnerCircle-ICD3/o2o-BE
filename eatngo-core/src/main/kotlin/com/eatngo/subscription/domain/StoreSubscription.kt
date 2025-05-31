package com.eatngo.subscription.domain

import com.eatngo.common.exception.StoreException
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

    /**
     * 구독 상태 확인
     */
    fun isActive(): Boolean = deletedAt == null

    /**
     * 재구독
     * 이미 활성화된 구독인 경우 예외 발생
     */
    fun restore() {
        if (deletedAt == null) {
            throw StoreException.SubscriptionAlreadyActive(id)
        }
        deletedAt = null
        updatedAt = LocalDateTime.now()
    }

    /**
     * 구독 상태 변경 (토글)
     * 현재 상태에 따라 구독 취소 또는 재구독 수행
     * @return 변경 후 구독 상태 (true: 활성화, false: 취소됨)
     */
    fun toggleSubscription(): Boolean {
        return if (isActive()) {
            false
        } else {
            restore()
            true
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