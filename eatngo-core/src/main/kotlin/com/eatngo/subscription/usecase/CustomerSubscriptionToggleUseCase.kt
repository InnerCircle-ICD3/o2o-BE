package com.eatngo.subscription.usecase

import com.eatngo.common.exception.StoreException
import com.eatngo.store.service.StoreService
import com.eatngo.subscription.domain.StoreSubscription
import com.eatngo.subscription.dto.StoreSubscriptionDto
import com.eatngo.subscription.event.SubscriptionCanceledEvent
import com.eatngo.subscription.event.SubscriptionCreatedEvent
import com.eatngo.subscription.event.SubscriptionResumedEvent
import com.eatngo.subscription.service.StoreSubscriptionService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 고객 구독 토글 유스케이스
 * 구독 생성, 취소, 재개 기능을 제공합니다.
 */
@Service
class CustomerSubscriptionToggleUseCase(
    private val storeSubscriptionService: StoreSubscriptionService,
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
        val dto = storeSubscriptionService.toggleSubscription(storeId, customerId)

        // 구독 상태에 따라 이벤트 발행
        when {
            // 구독이 활성화된 경우 (생성 또는 복구)
            dto.deletedAt == null && dto.createdAt == dto.updatedAt -> {
                // 최초 생성
                eventPublisher.publishEvent(
                    SubscriptionCreatedEvent(
                        subscriptionId = dto.id,
                        customerId = dto.userId,
                        storeId = dto.storeId,
                        timestamp = dto.createdAt
                    )
                )
            }
            dto.deletedAt == null && dto.createdAt != dto.updatedAt -> {
                // 재구독
                eventPublisher.publishEvent(
                    SubscriptionResumedEvent(
                        subscriptionId = dto.id,
                        customerId = dto.userId,
                        storeId = dto.storeId,
                        timestamp = dto.updatedAt
                    )
                )
            }
            // 구독이 비활성화(취소/soft delete)된 경우
            dto.deletedAt != null -> {
                eventPublisher.publishEvent(
                    SubscriptionCanceledEvent(
                        subscriptionId = dto.id,
                        customerId = dto.userId,
                        storeId = dto.storeId,
                        timestamp = dto.updatedAt
                    )
                )
            }
        }

        return dto
    }
}