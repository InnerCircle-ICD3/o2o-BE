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
    class StoreNotFound(storeId: String) : StoreException(
        BusinessErrorCode.STORE_NOT_FOUND,
        "${BusinessErrorCode.STORE_NOT_FOUND.message} (ID: $storeId)",
        mapOf("storeId" to storeId)
    )

    class StoreClosed(storeId: String) : StoreException(
        BusinessErrorCode.STORE_CLOSED,
        "${BusinessErrorCode.STORE_CLOSED.message} (ID: $storeId)",
        mapOf("storeId" to storeId)
    )

    class StoreNotAvailable(storeId: String) : StoreException(
        BusinessErrorCode.STORE_NOT_AVAILABLE,
        "${BusinessErrorCode.STORE_NOT_AVAILABLE.message} (ID: $storeId)",
        mapOf("storeId" to storeId)
    )

    // 매장 관리 관련 예외
    class StoreRegistrationFailed(reason: String) : StoreOwnerException(
        BusinessErrorCode.STORE_REGISTRATION_FAILED,
        "${BusinessErrorCode.STORE_REGISTRATION_FAILED.message}: $reason",
        mapOf("reason" to reason)
    )

    class StoreUpdateFailed(storeId: String, reason: String) : StoreOwnerException(
        BusinessErrorCode.STORE_UPDATE_FAILED,
        "${BusinessErrorCode.STORE_UPDATE_FAILED.message} (ID: $storeId): $reason",
        mapOf("storeId" to storeId, "reason" to reason)
    )

    class StoreDeleteFailed(storeId: String, reason: String) : StoreOwnerException(
        BusinessErrorCode.STORE_DELETE_FAILED,
        "${BusinessErrorCode.STORE_DELETE_FAILED.message} (ID: $storeId): $reason",
        mapOf("storeId" to storeId, "reason" to reason)
    )
}