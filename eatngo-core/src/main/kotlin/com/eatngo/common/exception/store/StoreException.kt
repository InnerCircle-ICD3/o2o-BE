package com.eatngo.common.exception.store

import com.eatngo.common.error.BusinessErrorCode
import com.eatngo.common.exception.BusinessException
import org.slf4j.event.Level

/**
 * 매장(점주) 관련 예외
 */
open class StoreException(
    override val errorCode: BusinessErrorCode,
    override val message: String = errorCode.message,
    override val data: Map<String, Any>? = null,
    override val logLevel: Level = Level.WARN,
) : BusinessException(errorCode, message, data, logLevel) {

    class StoreNotFound(storeId: Long) : StoreException(
        errorCode = BusinessErrorCode.STORE_NOT_FOUND,
        message = "${BusinessErrorCode.STORE_NOT_FOUND.message} (ID: $storeId)",
        data = mapOf("storeId" to storeId)
    )

    class StoreClosed(storeId: Long) : StoreException(
        errorCode = BusinessErrorCode.STORE_CLOSED,
        message = "${BusinessErrorCode.STORE_CLOSED.message} (ID: $storeId)",
        data = mapOf("storeId" to storeId)
    )

    class StoreNotAvailable(storeId: Long) : StoreException(
        errorCode = BusinessErrorCode.STORE_NOT_AVAILABLE,
        message = "${BusinessErrorCode.STORE_NOT_AVAILABLE.message} (ID: $storeId)",
        data = mapOf("storeId" to storeId)
    )

    class StoreNotFoundByStoreOwner(storeOwnerId: Long) : StoreException(
        errorCode = BusinessErrorCode.STORE_NOT_FOUND,
        message = "StoreOwnerId: $storeOwnerId) 의 ${BusinessErrorCode.STORE_NOT_FOUND.message}",
        data = mapOf("storeOwnerId" to storeOwnerId)
    )

    class StoreStatusInvalid(status: String) : StoreException(
        errorCode = BusinessErrorCode.STORE_STATUS_INVALID,
        message = "${BusinessErrorCode.STORE_STATUS_INVALID.message}: $status",
        data = mapOf("status" to status)
    )

    class Forbidden(storeOwnerId: Long) : StoreException(
        errorCode = BusinessErrorCode.STORE_OWNER_FORBIDDEN,
        message = "점주아이디: $storeOwnerId 는 ${BusinessErrorCode.STORE_OWNER_FORBIDDEN.message}",
        data = mapOf("storeOwnerId" to storeOwnerId)
    )
}