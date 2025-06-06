package com.eatngo.store.event.publisher

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.domain.Store
import com.eatngo.store.event.StoreEvent

/**
 * 매장 이벤트 발행을 위한 인터페이스
 * - 매장 관련 이벤트 발행을 추상화하여 의존성 분리
 */
interface StoreEventPublisher {
    
    /**
     * 매장 생성 이벤트 발행
     */
    fun publishStoreCreated(store: Store, userId: Long)
    
    /**
     * 매장 정보 업데이트 이벤트 발행
     */
    fun publishStoreUpdated(store: Store, userId: Long)
    
    /**
     * 매장 삭제 이벤트 발행
     */
    fun publishStoreDeleted(storeId: Long, userId: Long)
    
    /**
     * 매장 상태 변경 이벤트 발행
     */
    fun publishStoreStatusChanged(
        store: Store, 
        userId: Long, 
        previousStatus: StoreEnum.StoreStatus
    )
    
    /**
     * 매장 재고 상태 변경 이벤트 발행
     */
    fun publishStoreInventoryChanged(store: Store, hasStock: Boolean)
    
    /**
     * 이벤트 직접 발행 (필요한 경우)
     */
    fun publishEvent(event: StoreEvent)
} 