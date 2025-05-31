package com.eatngo.subscription.usecase

import com.eatngo.subscription.dto.StoreSubscriptionDto
import com.eatngo.subscription.event.SubscriptionEvent
import com.eatngo.subscription.event.SubscriptionEventStatus
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
        val dto = storeSubscriptionService.toggleSubscription(storeId, customerId)

        // 구독 상태에 따라 이벤트 발행
        when {
            // 구독이 활성화된 경우 (생성)
            dto.deletedAt == null && dto.createdAt == dto.updatedAt -> {
                eventPublisher.publishEvent(
                    SubscriptionEvent(
                        subscriptionId = dto.id,
                        customerId = dto.userId,
                        storeId = dto.storeId,
                        status = SubscriptionEventStatus.CREATED,
                        timestamp = dto.createdAt
                    )
                )
            }
            // 구독이 활성화된 경우 (재구독)
            dto.deletedAt == null && dto.createdAt != dto.updatedAt -> {
                eventPublisher.publishEvent(
                    SubscriptionEvent(
                        subscriptionId = dto.id,
                        customerId = dto.userId,
                        storeId = dto.storeId,
                        status = SubscriptionEventStatus.RESUMED,
                        timestamp = dto.updatedAt
                    )
                )
            }
            // 구독이 비활성화(취소/soft delete)된 경우
            dto.deletedAt != null -> {
                eventPublisher.publishEvent(
                    SubscriptionEvent(
                        subscriptionId = dto.id,
                        customerId = dto.userId,
                        storeId = dto.storeId,
                        status = SubscriptionEventStatus.CANCELED,
                        timestamp = dto.updatedAt
                    )
                )
            }
        }

        return dto
    }
}