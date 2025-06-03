package com.eatngo.subscription.usecase

import com.eatngo.common.constant.StoreEnum
import com.eatngo.subscription.dto.StoreSubscriptionDto
import com.eatngo.subscription.event.SubscriptionEvent
import com.eatngo.subscription.service.StoreSubscriptionService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 고객 구독 토글 유스케이스
 * 구독 생성, 취소, 재구독 기능을 제공
 */
@Service
class CustomerSubscriptionToggleUseCase(
    private val storeSubscriptionService: StoreSubscriptionService,
    private val eventPublisher: ApplicationEventPublisher
) {

    /**
     * 구독 토글 (생성/취소/재개)
     */
    @Transactional
    fun toggle(storeId: Long, customerId: Long): StoreSubscriptionDto {
        val (dto, operationType) = storeSubscriptionService.toggleSubscription(storeId, customerId)

        val eventTimestamp = when (operationType) {
            StoreEnum.SubscriptionStatus.CREATED -> dto.createdAt
            StoreEnum.SubscriptionStatus.RESUMED, StoreEnum.SubscriptionStatus.CANCELED -> dto.updatedAt
        }

        eventPublisher.publishEvent(
            SubscriptionEvent(
                subscriptionId = dto.id,
                customerId = dto.userId,
                storeId = dto.storeId,
                status = operationType,
                timestamp = eventTimestamp
            )
        )

        return dto
    }
}