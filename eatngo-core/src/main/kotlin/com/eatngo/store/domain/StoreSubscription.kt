package com.eatngo.store.domain

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
     * 재구독
     */
    fun restore() {
        deletedAt = null
        updatedAt = LocalDateTime.now()
    }
}