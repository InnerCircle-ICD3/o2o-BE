package com.eatngo.common.exception

import com.eatngo.common.error.BusinessErrorCode

/**
 * 점주 관련 예외
 */
open class StoreOwnerException(
    val errorCode: BusinessErrorCode,
    override val message: String = errorCode.message,
    val data: Map<String, Any>? = null,
) : RuntimeException(message) {

    // 점주 정보 관련 예외
    class StoreOwnerNotFound(ownerId: String) : StoreOwnerException(
        BusinessErrorCode.STORE_OWNER_NOT_FOUND,
        "${BusinessErrorCode.STORE_OWNER_NOT_FOUND.message} (ID: $ownerId)",
        mapOf("ownerId" to ownerId)
    )

    class StoreOwnerAlreadyExists(ownerId: String) : StoreOwnerException(
        BusinessErrorCode.STORE_OWNER_ALREADY_EXISTS,
        "${BusinessErrorCode.STORE_OWNER_ALREADY_EXISTS.message} (ID: $ownerId)",
        mapOf("ownerId" to ownerId)
    )

    // 매장 관리 관련 예외
    class StoreRegistrationFailed(reason: String) : StoreOwnerException(
        BusinessErrorCode.STORE_REGISTRATION_FAILED,
        "${BusinessErrorCode.STORE_REGISTRATION_FAILED.message}: $reason",
        mapOf("reason" to reason)
    )

    class StoreUpdateFailed(storeId: Long, reason: String) : StoreOwnerException(
        BusinessErrorCode.STORE_UPDATE_FAILED,
        "${BusinessErrorCode.STORE_UPDATE_FAILED.message} (ID: $storeId): $reason",
        mapOf("storeId" to storeId, "reason" to reason)
    )

    class StoreDeleteFailed(storeId: Long, reason: String) : StoreOwnerException(
        BusinessErrorCode.STORE_DELETE_FAILED,
        "${BusinessErrorCode.STORE_DELETE_FAILED.message} (ID: $storeId): $reason",
        mapOf("storeId" to storeId, "reason" to reason)
    )
}