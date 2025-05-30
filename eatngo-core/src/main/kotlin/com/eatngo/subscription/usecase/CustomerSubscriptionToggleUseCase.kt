package com.eatngo.subscription.usecase

import com.eatngo.common.exception.StoreException
import com.eatngo.store.infra.StorePersistence
import com.eatngo.subscription.domain.StoreSubscription
import com.eatngo.subscription.dto.StoreSubscriptionDto
import com.eatngo.subscription.event.SubscriptionCanceledEvent
import com.eatngo.subscription.event.SubscriptionCreatedEvent
import com.eatngo.subscription.event.SubscriptionResumedEvent
import com.eatngo.subscription.infra.StoreSubscriptionPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 고객 구독 토글 유스케이스
 * 구독 생성, 취소, 재개 기능을 제공합니다.
 */
@Service
class CustomerSubscriptionToggleUseCase(
    private val storeSubscriptionPersistence: StoreSubscriptionPersistence,
    private val storePersistence: StorePersistence,
    private val eventPublisher: ApplicationEventPublisher
) {

    /**
     * 구독 토글 (생성/취소/재개)
     * @param storeId 매장 ID
     * @param customerId 고객 ID
     * @return 구독 정보 DTO
     */
    @Transactional
    fun toggle(storeId: Long, customerId: Long): StoreSubscriptionDto {
        // 매장 존재 확인
        val store = storePersistence.findById(storeId)
            ?: throw StoreException.StoreNotFound(storeId)

        // 구독 존재 확인
        val subscription = storeSubscriptionPersistence.findByUserIdAndStoreId(customerId, storeId)?.let { existingSubscription ->
            // 구독이 존재하면 토글 (활성화 상태에 따라 취소 또는 재개)
            val wasActive = existingSubscription.isActive()
            existingSubscription.toggleSubscription()
            val savedSubscription = storeSubscriptionPersistence.save(existingSubscription)
            
            // 이벤트 발행
            if (wasActive) {
                eventPublisher.publishEvent(
                    SubscriptionCanceledEvent(
                        subscriptionId = savedSubscription.id,
                        customerId = savedSubscription.userId,
                        storeId = savedSubscription.storeId,
                        timestamp = savedSubscription.updatedAt
                    )
                )
            } else {
                eventPublisher.publishEvent(
                    SubscriptionResumedEvent(
                        subscriptionId = savedSubscription.id,
                        customerId = savedSubscription.userId,
                        storeId = savedSubscription.storeId,
                        timestamp = savedSubscription.updatedAt
                    )
                )
            }
            
            savedSubscription
        } ?: run {
            // 구독이 존재하지 않으면 새로 생성
            val newSubscription = StoreSubscription.create(customerId, storeId)
            val savedSubscription = storeSubscriptionPersistence.save(newSubscription)
            
            // 구독 생성 이벤트 발행
            eventPublisher.publishEvent(
                SubscriptionCreatedEvent(
                    subscriptionId = savedSubscription.id,
                    customerId = savedSubscription.userId,
                    storeId = savedSubscription.storeId,
                    timestamp = savedSubscription.createdAt
                )
            )
            
            savedSubscription
        }

        // DTO 변환 및 반환
        return StoreSubscriptionDto.from(
            subscription = subscription,
            storeName = store.name.value,
            mainImageUrl = store.imageUrl?.value,
            status = store.status
        )
    }
} 