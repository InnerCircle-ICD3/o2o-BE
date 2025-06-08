package com.eatngo.subscription.service

import com.eatngo.common.constant.StoreEnum
import com.eatngo.common.response.Cursor
import com.eatngo.subscription.dto.StoreSubscriptionDto
import com.eatngo.subscription.dto.StoreSubscriptionQueryParamDto

/**
 * 상점 구독 서비스 인터페이스
 */
interface StoreSubscriptionService {
    /**
     * 상점 구독 생성/취소
     */
    fun toggleSubscription(storeId: Long, customerId: Long): Pair<StoreSubscriptionDto, StoreEnum.SubscriptionStatus>

    /**
     * 커서 기반 구독 목록 조회 (무한 스크롤)
     */
    fun getSubscriptionsByQueryParameter(queryParam: StoreSubscriptionQueryParamDto): Cursor<StoreSubscriptionDto>
}