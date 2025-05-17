package com.eatngo.store.service

import com.eatngo.store.dto.StoreDto
import com.eatngo.store.dto.StoreSubscriptionCreateDto
import com.eatngo.store.dto.StoreSubscriptionDto
import com.eatngo.store.dto.StoreSubscriptionSummary
import com.eatngo.store.dto.StoreSummary

/**
 * 상점 구독 서비스 인터페이스
 */
interface StoreSubscriptionService {
    /**
     * 상점 구독 생성
     */
    suspend fun toggleSubscription(storeId: Long): StoreSubscriptionDto

    /**
     * 상점 구독 삭제 (Soft Delete)
     */
    suspend fun deleteSubscription(id: String): Boolean

    /**
     * ID로 상점 구독 조회
     */
    suspend fun getSubscriptionById(id: String): StoreSubscriptionDto
    
    /**
     * 사용자 ID로 상점 구독 목록 조회
     */
    suspend fun getSubscriptionsByUserId(userId: String, limit: Int, offset: Int): List<StoreSubscriptionSummary>
    
    /**
     * 매장 ID로 상점 구독 목록 조회
     */
    suspend fun getSubscriptionsByStoreId(storeId: Long, limit: Int, offset: Int): List<StoreSubscriptionSummary>

    /**
     * 내가 구독한 매장 목록 조회
     */
    suspend fun getMySubscribedStores(userId: Long): List<StoreSummary>
} 