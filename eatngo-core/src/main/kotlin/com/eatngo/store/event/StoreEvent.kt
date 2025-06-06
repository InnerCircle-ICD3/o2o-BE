package com.eatngo.store.event

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.domain.Store

/**
 * 매장 관련 이벤트의 기본 인터페이스
 */
interface StoreEvent {
    companion object {
        // 매장 생성 이벤트 (생성 시점)
        fun fromCreate(store: Store, userId: Long): StoreEvent? {
            return if (store.createdAt == store.updatedAt && store.deletedAt == null)
                StoreCUDEvent(
                    store = store,
                    userId = userId,
                    eventType = StoreCUDEventType.CREATED
                )
            else null
        }

        // 매장 삭제 이벤트 (삭제 시점)
        fun fromDelete(isDeleted: Boolean, storeId: Long, userId: Long): StoreEvent? {
            return if (isDeleted)
                StoreDeletedEvent(storeId, userId)
            else null
        }

        // 매장 상태 변경 이벤트 (상태가 변경된 경우)
        fun fromStatusChange(store: Store, userId: Long, previousStatus: StoreEnum.StoreStatus): StoreEvent? {
            if (store.status == previousStatus) return null
            return StoreStatusChangedEvent(
                store = store,
                userId = userId,
                previousStatus = previousStatus,
                currentStatus = store.status
            )
        }

        // 매장 정보 업데이트 이벤트
        fun fromInfoUpdate(store: Store, userId: Long): StoreEvent =
            StoreCUDEvent(
                store = store,
                userId = userId,
                eventType = StoreCUDEventType.UPDATED
            )
            
        // 재고 상태 변경에 따른 매장 상태 변경 이벤트
        fun fromInventoryChange(store: Store, hasStock: Boolean): StoreEvent {
            return StoreInventoryChangedEvent(
                store = store,
                hasStock = hasStock
            )
        }
    }
}

/**
 * 매장 생성/수정/삭제 이벤트 타입
 */
enum class StoreCUDEventType {
    CREATED,    // 매장 생성
    UPDATED,    // 매장 정보 업데이트
    DELETED     // 매장 삭제
}

/**
 * 매장 CUD(Create/Update/Delete) 이벤트
 * - 매장 생성, 정보 업데이트 이벤트를 통합 관리
 */
data class StoreCUDEvent(
    val store: Store,
    val userId: Long,
    val eventType: StoreCUDEventType
) : StoreEvent

/**
 * 매장 삭제 이벤트
 * - 삭제된 매장은 Store 객체가 없으므로 별도 처리
 */
data class StoreDeletedEvent(
    val storeId: Long,
    val userId: Long
) : StoreEvent

/**
 * 매장 상태 변경 이벤트
 * - OPEN, CLOSED, PENDING 상태 변경을 통합 관리
 */
data class StoreStatusChangedEvent(
    val store: Store,
    val userId: Long,
    val previousStatus: StoreEnum.StoreStatus,
    val currentStatus: StoreEnum.StoreStatus
) : StoreEvent

/**
 * 매장 재고 상태 변경 이벤트
 * - 재고 소진 및 재입고 상태 변경을 통합 관리
 */
data class StoreInventoryChangedEvent(
    val store: Store,
    val hasStock: Boolean
) : StoreEvent