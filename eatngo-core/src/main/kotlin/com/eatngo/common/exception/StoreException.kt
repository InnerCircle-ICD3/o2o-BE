package com.eatngo.common.exception

import com.eatngo.common.error.BusinessErrorCode

/**
 * 매장(점주) 관련 예외
 */
open class StoreException(
    val errorCode: BusinessErrorCode,
    override val message: String = errorCode.message,
    val data: Map<String, Any>? = null,
) : RuntimeException(message) {

    // 매장 조회 관련 예외
    class StoreNotFound(storeId: Long) : StoreException(
        BusinessErrorCode.STORE_NOT_FOUND,
        "${BusinessErrorCode.STORE_NOT_FOUND.message} (ID: $storeId)",
        mapOf("storeId" to storeId)
    )

    class StoreClosed(storeId: Long) : StoreException(
        BusinessErrorCode.STORE_CLOSED,
        "${BusinessErrorCode.STORE_CLOSED.message} (ID: $storeId)",
        mapOf("storeId" to storeId)
    )

    class StoreNotAvailable(storeId: Long) : StoreException(
        BusinessErrorCode.STORE_NOT_AVAILABLE,
        "${BusinessErrorCode.STORE_NOT_AVAILABLE.message} (ID: $storeId)",
        mapOf("storeId" to storeId)
    )
    
    class StoreNotAvailableForPickup(storeId: Long) : StoreException(
        BusinessErrorCode.STORE_NOT_AVAILABLE,
        "매장이 현재 픽업 가능한 시간이 아닙니다 (ID: $storeId)",
        mapOf("storeId" to storeId)
    )
    
    class StoreNotAvailableForTomorrow(storeId: Long) : StoreException(
        BusinessErrorCode.STORE_NOT_AVAILABLE,
        "매장이 내일 픽업을 지원하지 않습니다 (ID: $storeId)",
        mapOf("storeId" to storeId)
    )
}