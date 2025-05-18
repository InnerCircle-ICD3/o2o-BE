package com.eatngo.store.service

import com.eatngo.store.dto.StoreDto
import com.eatngo.store.dto.StoreSubscriptionDto

/**
 * 상점 구독 서비스 인터페이스
 */
interface StoreSubscriptionService {
    /**
     * 상점 구독 생성
     */
    suspend fun toggleSubscription(storeId: Long): StoreSubscriptionDto

    /**
     * ID로 상점 구독 조회
     */
    suspend fun getSubscriptionById(id: Long): StoreSubscriptionDto
    
    /**
     * 사용자 ID로 상점 구독 목록 조회
     */
    suspend fun getMySubscriptions(): List<StoreSubscriptionDto>
    
    /**
     * 매장 ID로 상점 구독 목록 조회
     */
    suspend fun getSubscriptionsByStoreId(storeId: Long): List<StoreSubscriptionDto>
} 