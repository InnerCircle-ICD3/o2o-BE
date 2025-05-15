package com.eatngo.store.infra

import com.eatngo.store.domain.StoreSubscription

/**
 * 매장 구독 영속성 인터페이스
 */
interface StoreSubscriptionPersistence {
    /**
     * ID로 매장 구독 조회
     */
    suspend fun findById(id: String): StoreSubscription?
    
    /**
     * 사용자 ID로 매장 구독 목록 조회
     */
    suspend fun findByUserId(userId: String, limit: Int, offset: Int): List<StoreSubscription>
    
    /**
     * 매장 ID로 매장 구독 목록 조회
     */
    suspend fun findByStoreId(storeId: Long, limit: Int, offset: Int): List<StoreSubscription>
    
    /**
     * 사용자 ID와 매장 ID로 매장 구독 조회
     */
    suspend fun findByUserIdAndStoreId(userId: String, storeId: Long): StoreSubscription?
    
    /**
     * 알림이 활성화된 구독 목록 조회
     */
    suspend fun findByNotificationEnabled(enabled: Boolean, limit: Int, offset: Int): List<StoreSubscription>
    
    /**
     * 특정 매장의 알림이 활성화된 구독 목록 조회
     */
    suspend fun findByStoreIdAndNotificationEnabled(storeId: Long, enabled: Boolean, limit: Int, offset: Int): List<StoreSubscription>
    
    /**
     * 매장 구독 저장
     */
    suspend fun save(subscription: StoreSubscription): StoreSubscription
    
    /**
     * 매장 구독 삭제
     */
    suspend fun softDelete(id: String): Boolean
    
    /**
     * 매장 구독 알림 상태 업데이트
     */
    suspend fun updateNotificationStatus(id: String, enabled: Boolean): Boolean
} 