package com.eatngo.store.serviceImpl

import com.eatngo.common.exception.StoreException
import com.eatngo.store.domain.StoreSubscription
import com.eatngo.store.dto.StoreDto
import com.eatngo.store.dto.StoreSubscriptionDto
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

    override suspend fun createSubscription(request: StoreSubscriptionDto.CreateRequest): StoreSubscriptionDto.Response {
        // 매장 존재 확인
        val store = storePersistence.findById(request.storeId) ?: throw StoreException.StoreNotFound(request.storeId)

        // 이미 구독 중인지 확인
        val existingSubscription = storeSubscriptionPersistence.findByUserIdAndStoreId(request.userId, request.storeId)
        if (existingSubscription != null) {
            //TODO: 이미 구독 중이면 구독 취소
        }

        val subscription = StoreSubscription(
            id = "", // 저장 시 생성됨
            userId = request.userId,
            storeId = request.storeId,
            notificationEnabled = request.notificationEnabled,
            createdAt = ZonedDateTime.now(),
            updatedAt = ZonedDateTime.now(),
            deletedAt = null
        )

        val savedSubscription = storeSubscriptionPersistence.save(subscription)
        val response = StoreSubscriptionDto.Response.from(savedSubscription, true)

        // 스토어 정보 추가
        val responseWithStore = response.copy(store = StoreDto.SummaryResponse.from(store))

        return responseWithStore
    }

    override suspend fun updateNotificationStatus(id: String, request: StoreSubscriptionDto.NotificationUpdateRequest): StoreSubscriptionDto.Response {
        val existingSubscription = storeSubscriptionPersistence.findById(id) ?: throw StoreException.SubscriptionNotFound(id)

        val updatedSubscription = if (request.notificationEnabled) {
            existingSubscription.enableNotifications()
        } else {
            existingSubscription.disableNotifications()
        }

        val savedSubscription = storeSubscriptionPersistence.save(updatedSubscription)

        // 스토어 정보 조회 및 추가
        val store = storePersistence.findById(existingSubscription.storeId)
        val response = StoreSubscriptionDto.Response.from(savedSubscription, true)

        return store?.let {
            response.copy(store = StoreDto.SummaryResponse.from(it))
        } ?: response
    }

    override suspend fun getSubscriptionById(id: String): StoreSubscriptionDto.Response {
        val subscription = storeSubscriptionPersistence.findById(id) ?: throw StoreException.SubscriptionNotFound(id)

        // 스토어 정보 조회 및 추가
        val store = storePersistence.findById(subscription.storeId)
        val response = StoreSubscriptionDto.Response.from(subscription, true)

        return store?.let {
            response.copy(store = StoreDto.SummaryResponse.from(it))
        } ?: response
    }

    override suspend fun getSubscriptionsByUserId(userId: String, limit: Int, offset: Int): List<StoreSubscriptionDto.SummaryResponse> {
        val subscriptions = storeSubscriptionPersistence.findByUserId(userId, limit, offset)
        return mapToSummaryResponses(subscriptions)
    }

    override suspend fun getSubscriptionsByStoreId(storeId: Long, limit: Int, offset: Int): List<StoreSubscriptionDto.SummaryResponse> {
        val subscriptions = storeSubscriptionPersistence.findByStoreId(storeId, limit, offset)
        return mapToSummaryResponses(subscriptions)
    }

    override suspend fun getSubscriptionByUserIdAndStoreId(userId: String, storeId: Long): StoreSubscriptionDto.Response? {
        val subscription = storeSubscriptionPersistence.findByUserIdAndStoreId(userId, storeId) ?: return null

        // 스토어 정보 조회 및 추가
        val store = storePersistence.findById(subscription.storeId)
        val response = StoreSubscriptionDto.Response.from(subscription, true)

        return store?.let {
            response.copy(store = StoreDto.SummaryResponse.from(it))
        } ?: response
    }

    override suspend fun getSubscriptionsByNotificationEnabled(enabled: Boolean, limit: Int, offset: Int): List<StoreSubscriptionDto.SummaryResponse> {
        val subscriptions = storeSubscriptionPersistence.findByNotificationEnabled(enabled, limit, offset)
        return mapToSummaryResponses(subscriptions)
    }

    override suspend fun getMySubscribedStores(userId: Long): StoreDto.SubscribedStoresResponse {
        // 사용자의 구독 목록 조회
        val subscriptions = storeSubscriptionPersistence.findByUserId(userId.toString(), 100, 0)

        // 구독한 매장 정보 조회
        val subscribedStores = mutableListOf<StoreDto.SummaryResponse>()

        for (subscription in subscriptions) {
            val store = storePersistence.findById(subscription.storeId) ?: continue
            subscribedStores.add(StoreDto.SummaryResponse.from(store))
        }

        return StoreDto.SubscribedStoresResponse(
            userId = userId,
            subscribedMarkets = subscribedStores
        )
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
    private suspend fun mapToSummaryResponses(subscriptions: List<StoreSubscription>): List<StoreSubscriptionDto.SummaryResponse> {
        val responses = mutableListOf<StoreSubscriptionDto.SummaryResponse>()

        for (subscription in subscriptions) {
            val store = storePersistence.findById(subscription.storeId)

            var response = StoreSubscriptionDto.SummaryResponse.from(subscription)

            // 스토어 정보 추가
            store?.let {
                response = response.copy(store = StoreDto.SummaryResponse.from(it))
            }

            responses.add(response)
        }

        return responses
    }
}