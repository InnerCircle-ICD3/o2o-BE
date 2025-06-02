package com.eatngo.subscription.usecase

import com.eatngo.common.exception.StoreException
import com.eatngo.store.infra.StorePersistence
import com.eatngo.store.service.StoreService
import com.eatngo.subscription.dto.StoreSubscriptionDto
import com.eatngo.subscription.infra.StoreSubscriptionPersistence
import com.eatngo.subscription.service.StoreSubscriptionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 고객 구독 조회 유스케이스
 * 고객 관점에서의 구독 목록 및 개별 구독 조회 기능을 제공합니다.
 */
@Service
class CustomerSubscriptionQueryUseCase(
    private val storeSubscriptionService: StoreSubscriptionService,
) {
    /**
     * 고객 ID로 구독 목록 조회
     */
    @Transactional(readOnly = true)
    fun getSubscriptionsByCustomerId(customerId: Long): List<StoreSubscriptionDto> {
        return storeSubscriptionService.getMySubscriptions(customerId)
    }
} 