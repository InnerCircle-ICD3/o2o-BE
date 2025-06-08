package com.eatngo.subscription.usecase

import com.eatngo.common.response.Cursor
import com.eatngo.subscription.dto.StoreOwnerSubscriptionQueryParamDto
import com.eatngo.subscription.dto.StoreSubscriptionDto
import com.eatngo.subscription.service.StoreSubscriptionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 점주 구독 조회 유스케이스
 */
@Service
class StoreOwnerSubscriptionQueryUseCase(
    private val storeSubscriptionService: StoreSubscriptionService
) {
    /**
     * 커서 기반 구독자 목록 조회 (무한 스크롤)
     */
    @Transactional(readOnly = true)
    fun getSubscriptionsByQueryParameter(queryParam: StoreOwnerSubscriptionQueryParamDto): Cursor<StoreSubscriptionDto> {
        return storeSubscriptionService.getSubscriptionsByQueryParameter(queryParam)
    }
} 