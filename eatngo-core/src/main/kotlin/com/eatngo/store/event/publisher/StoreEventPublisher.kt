package com.eatngo.store.event.publisher

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.event.StoreCUDEventType

/**
 * 매장 이벤트 발행을 위한 인터페이스
 */
interface StoreEventPublisher {
    
    /**
     * 매장 CUD 이벤트 발행
     */
    fun publishStoreCUDEvent(storeId: Long, userId: Long, eventType: StoreCUDEventType)
    
    /**
     * 매장 상태 변경 이벤트 발행
     */
    fun publishStoreStatusChanged(
        storeId: Long,
        userId: Long, 
        previousStatus: StoreEnum.StoreStatus,
        currentStatus: StoreEnum.StoreStatus
    )
} 