package com.eatngo.store.serviceImpl

import com.eatngo.common.exception.StoreException
import com.eatngo.store.domain.StoreSubscription
import com.eatngo.store.dto.StoreDto
import com.eatngo.store.dto.StoreSubscriptionCreateDto
import com.eatngo.store.dto.StoreSubscriptionDto
import com.eatngo.store.dto.StoreSubscriptionSummary
import com.eatngo.store.dto.StoreSummary
import com.eatngo.store.dto.SubscribedStoresResponse
import com.eatngo.store.infra.StorePersistence
import com.eatngo.store.infra.StoreSubscriptionPersistence
import com.eatngo.store.service.StoreSubscriptionService
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

/**
 * 상점 구독 서비스 구현체
 */
@Service
class StoreSubscriptionServiceImpl(
    private val storeSubscriptionPersistence: StoreSubscriptionPersistence,
    private val storePersistence: StorePersistence
) : StoreSubscriptionService {

    override suspend fun toggleSubscription(storeId: Long): StoreSubscriptionDto {
        val userId = "임시사용자" //TODO: 나중에 security context 에서 가져오는 유틸 생기면 바꾸기

        // 매장 존재 확인
        val store = storePersistence.findById(storeId) ?: throw StoreException.StoreNotFound(storeId)

        // 기존 구독 조회
        val existingSubscription = storeSubscriptionPersistence.findByUserIdAndStoreId(userId, storeId)

        return if (existingSubscription != null) {
            existingSubscription.softDelete()
            val saved = storeSubscriptionPersistence.save(existingSubscription)
            StoreSubscriptionDto.from(saved, store)
        } else {
            val newSubscription = StoreSubscription(  // id와 updatedAt 자동 할당
                userId = userId,
                storeId = storeId
            )
            val saved = storeSubscriptionPersistence.save(newSubscription)
            StoreSubscriptionDto.from(saved, store)
        }
    }

    override suspend fun getSubscriptionById(id: String): StoreSubscriptionDto {
        val subscription = storeSubscriptionPersistence.findById(id) ?: throw StoreException.SubscriptionNotFound(id)

        // 스토어 정보 조회 및 추가
        val store = storePersistence.findById(subscription.storeId)
        return StoreSubscriptionDto.from(subscription, store)
    }

    override suspend fun getSubscriptionsByUserId(userId: String, limit: Int, offset: Int): List<StoreSubscriptionSummary> {
        val subscriptions = storeSubscriptionPersistence.findByUserId(userId, limit, offset)
        return mapToSummaryResponses(subscriptions)
    }

    override suspend fun getSubscriptionsByStoreId(storeId: Long, limit: Int, offset: Int): List<StoreSubscriptionSummary> {
        val subscriptions = storeSubscriptionPersistence.findByStoreId(storeId, limit, offset)
        return mapToSummaryResponses(subscriptions)
    }

    override suspend fun getMySubscribedStores(userId: Long): List<StoreSummary> {
        val subscriptions = storeSubscriptionPersistence.findByUserId(
            userId = userId.toString(),
            limit = 100,
            offset = 0
        )

        return storePersistence.findAllByIds(subscriptions.map { it.storeId.toString() })
            .map { StoreSummary.from(it) }
    }

    override suspend fun deleteSubscription(id: String): Boolean {
        val subscription = storeSubscriptionPersistence.findById(id) ?: throw StoreException.SubscriptionNotFound(id)

        // Soft Delete 적용
        val deletedSubscription = subscription.softDelete()
        storeSubscriptionPersistence.save(deletedSubscription)

        return true
    }

    /**
     * 구독 목록을 SummaryResponse 목록으로 변환
     */
    private suspend fun mapToSummaryResponses(subscriptions: List<StoreSubscription>): List<StoreSubscriptionSummary> {
        val responses = mutableListOf<StoreSubscriptionSummary>()

        for (subscription in subscriptions) {
            val store = storePersistence.findById(subscription.storeId)
            responses.add(StoreSubscriptionSummary.from(subscription, store))
        }

        return responses
    }
}