package com.eatngo.subscription.dto

/**
 * 구독 목록 조회용 쿼리 파라미터 인터페이스
 */
interface StoreSubscriptionQueryParamDto {
    val lastId: Long?
}

/**
 * 고객용 구독 목록 조회 파라미터
 */
data class CustomerSubscriptionQueryParamDto(
    val customerId: Long,
    override val lastId: Long? = null,
) : StoreSubscriptionQueryParamDto

/**
 * 점주용 구독자 목록 조회 파라미터
 */
data class StoreOwnerSubscriptionQueryParamDto(
    val storeId: Long,
    val storeOwnerId: Long,
    override val lastId: Long? = null,
) : StoreSubscriptionQueryParamDto 