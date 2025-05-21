package com.eatngo.store.service

import com.eatngo.store.dto.StoreDto
import com.eatngo.store.dto.StoreSubscriptionDto

/**
 * 상점 구독 서비스 인터페이스
 */
interface StoreSubscriptionService {
    /**
     * 상점 구독 생성/취소
     */
    fun toggleSubscription(storeId: Long): StoreSubscriptionDto

    /**
     * ID로 상점 구독 조회
     */
    fun getSubscriptionById(id: Long): StoreSubscriptionDto
    
    /**
     * 사용자 ID로 상점 구독 목록 조회
     */
    fun getMySubscriptions(): List<StoreSubscriptionDto>
    
    /**
     * 매장 ID로 상점 구독 목록 조회
     */
    fun getSubscriptionsByStoreId(storeId: Long): List<StoreSubscriptionDto>
} 