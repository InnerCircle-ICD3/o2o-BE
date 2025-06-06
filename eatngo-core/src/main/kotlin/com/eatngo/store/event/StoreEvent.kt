package com.eatngo.store.event

import com.eatngo.common.constant.StoreEnum

/**
 * 매장 CUD(Create/Update/Delete) 이벤트 타입
 */
enum class StoreCUDEventType {
    CREATED,    // 매장 생성
    UPDATED,    // 매장 정보 업데이트
    DELETED     // 매장 삭제
}

/**
 * 매장 CUD(Create/Update/Delete) 통합 이벤트
 * 검색 시스템을 위한 단일 이벤트
 */
data class StoreCUDEvent(
    val storeId: Long,
    val userId: Long,
    val eventType: StoreCUDEventType
)

/**
 * 매장 상태 변경 이벤트
 */
data class StoreStatusChangedEvent(
    val storeId: Long,
    val userId: Long,
    val previousStatus: StoreEnum.StoreStatus,
    val currentStatus: StoreEnum.StoreStatus
)

/**
 * 매장 재고 상태 변경 이벤트
 */
data class StoreInventoryChangedEvent(
    val storeId: Long,
    val hasStock: Boolean
)