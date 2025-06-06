package com.eatngo.store.event.publisher

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.event.StoreCUDEvent
import com.eatngo.store.event.StoreCUDEventType
import com.eatngo.store.event.StoreStatusChangedEvent
import org.slf4j.LoggerFactory
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
        
        val eventTypeMsg = when (eventType) {
            StoreCUDEventType.CREATED -> "생성"
            StoreCUDEventType.UPDATED -> "정보 업데이트"
            StoreCUDEventType.DELETED -> "삭제"
        }
        
        log.info("매장 {} 이벤트 발행: storeId={}, userId={}", eventTypeMsg, storeId, userId)
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
        log.info("매장 상태 변경 이벤트 발행: storeId={}, previousStatus={}, currentStatus={}", 
            storeId, previousStatus, currentStatus)
        eventPublisher.publishEvent(event)
    }

    companion object {
        private val log = LoggerFactory.getLogger(DirectStoreEventPublisher::class.java)
    }
} 