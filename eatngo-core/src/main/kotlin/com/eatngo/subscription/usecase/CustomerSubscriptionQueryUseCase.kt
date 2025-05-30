package com.eatngo.subscription.usecase

import com.eatngo.common.exception.StoreException
import com.eatngo.store.infra.StorePersistence
import com.eatngo.subscription.dto.StoreSubscriptionDto
import com.eatngo.subscription.infra.StoreSubscriptionPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 고객 구독 조회 유스케이스
 * 고객 관점에서의 구독 목록 및 개별 구독 조회 기능을 제공합니다.
 */
@Service
class CustomerSubscriptionQueryUseCase(
    private val storeSubscriptionPersistence: StoreSubscriptionPersistence,
    private val storePersistence: StorePersistence
) {

    /**
     * 구독 ID로 구독 정보 조회
     * @param subscriptionId 구독 ID
     * @param customerId 고객 ID (권한 검증용)
     * @return 구독 정보 DTO
     */
    @Transactional(readOnly = true)
    fun getSubscriptionById(subscriptionId: Long, customerId: Long): StoreSubscriptionDto {
        val subscription = storeSubscriptionPersistence.findById(subscriptionId)
            ?: throw StoreException.SubscriptionNotFound(subscriptionId)
        
        // 권한 검증
        subscription.validateOwnership(customerId)
        
        val store = storePersistence.findById(subscription.storeId)
            ?: throw StoreException.StoreNotFound(subscription.storeId)
        
        return StoreSubscriptionDto.from(
            subscription = subscription,
            storeName = store.name.value,
            mainImageUrl = store.imageUrl?.value,
            status = store.status
        )
    }

    /**
     * 고객 ID로 구독 목록 조회
     * @param customerId 고객 ID
     * @return 구독 정보 DTO 목록
     */
    @Transactional(readOnly = true)
    fun getSubscriptionsByCustomerId(customerId: Long): List<StoreSubscriptionDto> {
        // 사용자의 구독 목록 조회
        val subscriptions = storeSubscriptionPersistence.findByUserId(customerId)
        
        if (subscriptions.isEmpty()) {
            return emptyList()
        }
        
        // 구독된 모든 매장 ID 추출
        val storeIds = subscriptions.map { it.storeId }
        
        // 매장 정보를 한 번에 조회 (N+1 문제 해결)
        val stores = storePersistence.findAllByIds(storeIds).associateBy { it.id }
        
        // 각 구독 정보에 매장 정보를 매핑하여 DTO 반환
        return subscriptions.map { subscription ->
            val store = stores[subscription.storeId] ?: throw StoreException.StoreNotFound(subscription.storeId)
            StoreSubscriptionDto.from(
                subscription = subscription,
                storeName = store.name.value,
                mainImageUrl = store.imageUrl?.value,
                status = store.status
            )
        }
    }
} 