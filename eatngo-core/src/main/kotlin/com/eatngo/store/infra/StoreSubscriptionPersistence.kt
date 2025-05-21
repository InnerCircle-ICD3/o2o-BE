package com.eatngo.store.infra

import com.eatngo.store.domain.StoreSubscription

/**
 * 매장 구독 영속성 인터페이스
 */
interface StoreSubscriptionPersistence {
    /**
     * 구독 ID로 상점 구독 조회
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
     * 사용자 ID와 상점 ID로 상점 구독 조회
     */
    fun findByUserIdAndStoreId(userId: Long, storeId: Long): StoreSubscription?

    /**
     * 상점 구독 저장
     */
    fun save(subscription: StoreSubscription): StoreSubscription

    /**
     * 상점 구독 삭제
     */
    fun softDelete(id: Long): Boolean

    /**
     * 사용자 ID로 상점 구독 여부 조회
     */
    fun existsByUserIdAndStoreId(userId: Long, storeId: Long): Boolean
} 