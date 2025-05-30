package com.eatngo.subscription.usecase

import com.eatngo.common.exception.StoreException
import com.eatngo.store.infra.StorePersistence
import com.eatngo.subscription.dto.StoreSubscriptionDto
import com.eatngo.subscription.infra.StoreSubscriptionPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 점주용 구독 조회 유스케이스
 * 점주 관점에서의 구독자 목록 조회 기능을 제공합니다.
 */
@Service
class StoreOwnerSubscriptionQueryUseCase(
    private val storeSubscriptionPersistence: StoreSubscriptionPersistence,
    private val storePersistence: StorePersistence
) {

    /**
     * 매장 ID로 구독자 목록 조회
     * @param storeId 매장 ID
     * @param storeOwnerId 점주 ID (권한 검증용)
     * @return 구독 정보 DTO 목록
     */
    @Transactional(readOnly = true)
    fun getSubscriptionsByStoreId(storeId: Long, storeOwnerId: Long): List<StoreSubscriptionDto> {
        // 매장 존재 확인 및 권한 검증
        val store = storePersistence.findById(storeId)
            ?: throw StoreException.StoreNotFound(storeId)
        
        // 매장 소유자 검증
        if (store.storeOwnerId != storeOwnerId) {
            throw StoreException.Forbidden(storeOwnerId)
        }
        
        // 매장의 구독자 목록 조회
        val subscriptions = storeSubscriptionPersistence.findByStoreId(storeId)
        
        if (subscriptions.isEmpty()) {
            return emptyList()
        }
        
        // 각 구독 정보에 매장 정보를 매핑하여 DTO 반환
        return subscriptions.map { subscription ->
            StoreSubscriptionDto.from(
                subscription = subscription,
                storeName = store.name.value,
                mainImageUrl = store.imageUrl?.value,
                status = store.status
            )
        }
    }

    /**
     * 매장 ID로 구독자 수 조회
     * @param storeId 매장 ID
     * @param storeOwnerId 점주 ID (권한 검증용)
     * @return 구독자 수
     */
    @Transactional(readOnly = true)
    fun getSubscriptionCountByStoreId(storeId: Long, storeOwnerId: Long): Int {
        // 매장 존재 확인 및 권한 검증
        val store = storePersistence.findById(storeId)
            ?: throw StoreException.StoreNotFound(storeId)
        
        // 매장 소유자 검증
        if (store.storeOwnerId != storeOwnerId) {
            throw StoreException.Forbidden(storeOwnerId)
        }
        
        // 매장의 구독자 목록 조회 및 개수 반환
        return storeSubscriptionPersistence.findByStoreId(storeId).size
    }
} 