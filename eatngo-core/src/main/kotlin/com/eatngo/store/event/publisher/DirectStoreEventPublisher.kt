package com.eatngo.store.event.publisher

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.domain.Store
import com.eatngo.store.event.StoreEvent
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

/**
 * Spring의 ApplicationEventPublisher를 사용하여 매장 이벤트를 직접 발행하는 구현체
 */
@Component
class DirectStoreEventPublisher(
    private val eventPublisher: ApplicationEventPublisher
) : StoreEventPublisher {

    override fun publishStoreCreated(store: Store, userId: Long) {
        StoreEvent.fromCreate(store, userId)?.let { event ->
            log.info("매장 생성 이벤트 발행: storeId={}, userId={}", store.id, userId)
            eventPublisher.publishEvent(event)
        }
    }

    override fun publishStoreUpdated(store: Store, userId: Long) {
        val event = StoreEvent.fromInfoUpdate(store, userId)
        log.info("매장 정보 업데이트 이벤트 발행: storeId={}, userId={}", store.id, userId)
        eventPublisher.publishEvent(event)
    }

    override fun publishStoreDeleted(storeId: Long, userId: Long) {
        StoreEvent.fromDelete(true, storeId, userId)?.let { event ->
            log.info("매장 삭제 이벤트 발행: storeId={}, userId={}", storeId, userId)
            eventPublisher.publishEvent(event)
        }
    }

    override fun publishStoreStatusChanged(
        store: Store,
        userId: Long,
        previousStatus: StoreEnum.StoreStatus
    ) {
        StoreEvent.fromStatusChange(store, userId, previousStatus)?.let { event ->
            log.info("매장 상태 변경 이벤트 발행: storeId={}, previousStatus={}, currentStatus={}", 
                store.id, previousStatus, store.status)
            eventPublisher.publishEvent(event)
        }
    }

    override fun publishStoreInventoryChanged(store: Store, hasStock: Boolean) {
        val event = StoreEvent.fromInventoryChange(store, hasStock)
        val statusMsg = if (hasStock) "재고 복구" else "재고 소진"
        log.info("매장 재고 상태 변경 이벤트 발행: storeId={}, 상태={}", store.id, statusMsg)
        eventPublisher.publishEvent(event)
    }

    override fun publishEvent(event: StoreEvent) {
        log.info("커스텀 매장 이벤트 발행: eventType={}", event::class.simpleName)
        eventPublisher.publishEvent(event)
    }
    
    companion object {
        private val log = LoggerFactory.getLogger(DirectStoreEventPublisher::class.java)
    }
} 