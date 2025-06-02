package com.eatngo.subscription.service

import com.eatngo.common.constant.StoreEnum
import com.eatngo.subscription.dto.StoreSubscriptionDto

/**
 * 상점 구독 서비스 인터페이스
 */
interface StoreSubscriptionService {
    /**
     * 상점 구독 생성/취소
     */
    fun toggleSubscription(storeId: Long, customerId: Long): Pair<StoreSubscriptionDto, StoreEnum.SubscriptionStatus>

    /**
     * ID로 상점 구독 조회
     */
    fun getSubscriptionById(id: Long): StoreSubscriptionDto

    /**
     * 사용자 ID로 상점 구독 목록 조회
     */
    fun getMySubscriptions(customerId: Long): List<StoreSubscriptionDto>

    /**
     * 매장 ID로 상점 구독 목록 조회
     */
    fun getSubscriptionsByStoreId(storeId: Long, storeOwnerId: Long): List<StoreSubscriptionDto>
}