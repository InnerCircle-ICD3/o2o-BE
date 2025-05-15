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
    suspend fun createSubscription(request: StoreSubscriptionDto.CreateRequest): StoreSubscriptionDto.Response
    
    /**
     * 알림 상태 변경
     */
    suspend fun updateNotificationStatus(id: String, request: StoreSubscriptionDto.NotificationUpdateRequest): StoreSubscriptionDto.Response
    
    /**
     * ID로 상점 구독 조회
     */
    suspend fun getSubscriptionById(id: String): StoreSubscriptionDto.Response
    
    /**
     * 사용자 ID로 상점 구독 목록 조회
     */
    suspend fun getSubscriptionsByUserId(userId: String, limit: Int, offset: Int): List<StoreSubscriptionDto.SummaryResponse>
    
    /**
     * 매장 ID로 상점 구독 목록 조회
     */
    suspend fun getSubscriptionsByStoreId(storeId: Long, limit: Int, offset: Int): List<StoreSubscriptionDto.SummaryResponse>
    
    /**
     * 사용자 ID와 상점 ID로 구독 조회
     */
    suspend fun getSubscriptionByUserIdAndStoreId(userId: String, storeId: Long): StoreSubscriptionDto.Response?
    
    /**
     * 알림 활성화 상태로 구독 목록 조회
     */
    suspend fun getSubscriptionsByNotificationEnabled(enabled: Boolean, limit: Int, offset: Int): List<StoreSubscriptionDto.SummaryResponse>
    
    /**
     * 내가 구독한 매장 목록 조회
     */
    suspend fun getMySubscribedStores(userId: Long): StoreDto.SubscribedStoresResponse
    
    /**
     * 상점 구독 삭제 (Soft Delete)
     */
    suspend fun deleteSubscription(id: String): Boolean
} 