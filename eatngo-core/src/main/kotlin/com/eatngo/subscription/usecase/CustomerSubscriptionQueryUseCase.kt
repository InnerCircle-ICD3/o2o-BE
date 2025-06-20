package com.eatngo.subscription.usecase

import com.eatngo.common.response.Cursor
import com.eatngo.subscription.dto.CustomerSubscriptionQueryParamDto
import com.eatngo.subscription.dto.StoreSubscriptionDto
import com.eatngo.subscription.dto.StoreSubscriptionQueryParamDto
import com.eatngo.subscription.service.StoreSubscriptionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 고객 구독 조회 유스케이스
 */
@Service
class CustomerSubscriptionQueryUseCase(
    private val storeSubscriptionService: StoreSubscriptionService
) {
    /**
     * 고객 ID로 구독 목록 커서 기반 조회 (무한 스크롤)
     */
    @Transactional(readOnly = true)
    fun getSubscriptionsByQueryParameter(queryParam: CustomerSubscriptionQueryParamDto): Cursor<StoreSubscriptionDto> {
        return storeSubscriptionService.getSubscriptionsByQueryParameter(queryParam)
    }

    /**
     * 고객이 구독하고 있는 매장 ID 목록 조회 (메인 화면 최적화용)
     */
    @Transactional(readOnly = true)
    fun getSubscribedStoreIds(customerId: Long): List<Long> {
        return storeSubscriptionService.getSubscribedStoreIds(customerId)
    }
} 