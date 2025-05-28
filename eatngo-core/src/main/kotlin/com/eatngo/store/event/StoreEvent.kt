package com.eatngo.store.event

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.domain.Store

interface StoreEvent {
    companion object {
        // 매장 생성 이벤트 (생성 시점)
        fun fromCreate(store: Store, userId: Long): StoreEvent? {
            return if (store.createdAt == store.updatedAt && store.deletedAt == null)
                StoreCreatedEvent(store, userId)
            else null
        }

        // 매장 삭제 이벤트 (삭제 시점)
        fun fromDelete(store: Store, userId: Long): StoreEvent? {
            return if (store.deletedAt != null)
                StoreDeletedEvent(store, userId)
            else null
        }

        // 매장 상태 변경 이벤트 (상태가 변경된 경우)
        fun fromStatusChange(store: Store, userId: Long, previousStatus: StoreEnum.StoreStatus): StoreEvent? {
            if (store.status == previousStatus) return null
            return when (store.status) {
                StoreEnum.StoreStatus.OPEN -> StoreOpenedEvent(store, userId)
                StoreEnum.StoreStatus.CLOSED -> StoreClosedEvent(store, userId)
                StoreEnum.StoreStatus.PENDING -> StorePendingEvent(store, userId)
            }.let { statusEvent ->
                //TODO
                statusEvent
            }
        }

        // 매장 정보 업데이트 이벤트
        fun fromInfoUpdate(store: Store, userId: Long): StoreEvent =
            StoreUpdatedEvent(store, userId)

        // 픽업 정보 업데이트 이벤트
        fun fromPickupUpdate(store: Store, userId: Long): StoreEvent =
            StorePickupInfoUpdatedEvent(store, userId)
    }
}

/**
 * 매장 생성 이벤트
 */
data class StoreCreatedEvent(
    val store: Store,
    val userId: Long
) : StoreEvent

/**
 * 매장 정보 업데이트 이벤트
 */
data class StoreUpdatedEvent(
    val store: Store,
    val userId: Long
) : StoreEvent

/**
 * 매장 상태 변경 이벤트
 */
data class StoreStatusChangedEvent(
    val store: Store,
    val userId: Long,
    val previousStatus: StoreEnum.StoreStatus,
    val currentStatus: StoreEnum.StoreStatus
) : StoreEvent

/**
 * 매장 열림 이벤트
 */
data class StoreOpenedEvent(
    val store: Store,
    val userId: Long
) : StoreEvent

/**
 * 매장 닫힘 이벤트
 */
data class StoreClosedEvent(
    val store: Store,
    val userId: Long
) : StoreEvent

/**
 * 매장 승인대기 이벤트
 */
data class StorePendingEvent(
    val store: Store,
    val userId: Long
) : StoreEvent

/**
 * 매장 픽업 정보 업데이트 이벤트
 */
data class StorePickupInfoUpdatedEvent(
    val store: Store,
    val userId: Long
) : StoreEvent

/**
 * 매장 삭제 이벤트
 */
data class StoreDeletedEvent(
    val store: Store,
    val userId: Long
) : StoreEvent