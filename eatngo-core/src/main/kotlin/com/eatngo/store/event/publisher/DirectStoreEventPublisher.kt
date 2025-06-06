package com.eatngo.store.event.publisher

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.event.StoreCUDEvent
import com.eatngo.store.event.StoreCUDEventType
import com.eatngo.store.event.StoreStatusChangedEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

/**
 * 매장 이벤트 발행 구현체
 */
@Component
class DirectStoreEventPublisher(
    private val eventPublisher: ApplicationEventPublisher
) : StoreEventPublisher {

    override fun publishStoreCUDEvent(storeId: Long, userId: Long, eventType: StoreCUDEventType) {
        val event = StoreCUDEvent(
            storeId = storeId,
            userId = userId,
            eventType = eventType
        )

        eventPublisher.publishEvent(event)
    }

    override fun publishStoreStatusChanged(
        storeId: Long,
        userId: Long,
        previousStatus: StoreEnum.StoreStatus,
        currentStatus: StoreEnum.StoreStatus
    ) {
        val event = StoreStatusChangedEvent(
            storeId = storeId,
            userId = userId,
            previousStatus = previousStatus,
            currentStatus = currentStatus
        )

        eventPublisher.publishEvent(event)
    }
} 