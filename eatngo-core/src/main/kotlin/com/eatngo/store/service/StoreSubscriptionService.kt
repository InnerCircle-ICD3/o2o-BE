package com.eatngo.store.service

import com.eatngo.store.dto.CustomerStoreSubscriptionListResponse
import com.eatngo.store.dto.StoreDto
import com.eatngo.store.dto.StoreSubscriptionCreateDto
import com.eatngo.store.dto.StoreSubscriptionDto
import com.eatngo.store.dto.StoreSubscriptionSummary
import com.eatngo.store.dto.StoreSummary
import com.eatngo.store.dto.SubscriptionToggleResponse

/**
 * 상점 구독 서비스 인터페이스
 */
interface StoreSubscriptionService {
    /**
     * 상점 구독 생성
     */
    suspend fun toggleSubscription(storeId: Long): StoreSubscriptionDto
    
    /**
     * 토글 구독 응답 DTO 생성
     */
    suspend fun toggleSubscriptionWithResponse(storeId: Long): SubscriptionToggleResponse

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
     * 사용자 ID로 구독 목록 조회 및 응답 DTO 생성
     */
    suspend fun getSubscriptionsByUserIdWithResponse(userId: String, limit: Int, offset: Int): CustomerStoreSubscriptionListResponse
    
    /**
     * 매장 ID로 상점 구독 목록 조회
     */
    suspend fun getSubscriptionsByStoreId(storeId: Long, limit: Int, offset: Int): List<StoreSubscriptionSummary>

    /**
     * 내가 구독한 매장 목록 조회
     */
    suspend fun getMySubscribedStores(userId: Long): List<StoreSummary>
} 