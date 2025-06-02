package com.eatngo.subscription.usecase

import com.eatngo.common.exception.StoreException
import com.eatngo.store.infra.StorePersistence
import com.eatngo.subscription.dto.StoreSubscriptionDto
import com.eatngo.subscription.infra.StoreSubscriptionPersistence
import com.eatngo.subscription.service.StoreSubscriptionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 점주용 구독 조회 유스케이스
 * 점주 관점에서의 구독자 목록 조회 기능을 제공합니다.
 */
@Service
class StoreOwnerSubscriptionQueryUseCase(
    private val storeSubscriptionService: StoreSubscriptionService,
) {

    /**
     * 매장 ID로 구독자 목록 조회
     */
    @Transactional(readOnly = true)
    fun getSubscriptionsByStoreId(storeId: Long, storeOwnerId: Long): List<StoreSubscriptionDto> {
        return storeSubscriptionService.getSubscriptionsByStoreId(storeId, storeOwnerId)
    }
} 