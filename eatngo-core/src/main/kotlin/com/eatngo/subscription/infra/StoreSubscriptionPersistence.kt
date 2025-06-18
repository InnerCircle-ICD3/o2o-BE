package com.eatngo.subscription.infra

import com.eatngo.common.response.Cursor
import com.eatngo.subscription.domain.StoreSubscription
import com.eatngo.subscription.dto.StoreSubscriptionQueryParamDto

/**
 * 매장 구독 영속성 인터페이스
 */
interface StoreSubscriptionPersistence {
    /**
     * 구독 ID로 상점 구독 조회(활성구독만)
     */
    fun findById(id: Long): StoreSubscription?

    /**
     * 사용자 ID로 상점 구독 목록 조회
     */
    fun findByUserId(userId: Long): List<StoreSubscription>

    /**
     * 상점 ID로 상점 구독 목록 조회
     */
    fun findByStoreId(storeId: Long): List<StoreSubscription>

    /**
     * 사용자 ID와 상점 ID로 상점 구독 조회 - deleted 된 것도 전부 조회
     */
    fun findAllByUserIdAndStoreId(userId: Long?, storeId: Long): StoreSubscription?

    /**
     * 상점 구독 저장
     */
    fun save(subscription: StoreSubscription): StoreSubscription

    /**
     * 매장 ID 목록으로 구독 정보 일괄 조회 (N+1 문제 해결)
     */
    fun findAllByStoreIds(storeIds: List<Long>): List<StoreSubscription>

    /**
     * 사용자 ID로 구독한 매장 ID 목록 조회
     */
    fun findStoreIdsByUserId(userId: Long): List<Long>

    /**
     * 커서 기반 구독 목록 조회 (무한 스크롤)
     */
    fun findAllByQueryParameter(queryParam: StoreSubscriptionQueryParamDto): Cursor<StoreSubscription>
}