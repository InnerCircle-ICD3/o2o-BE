package com.eatngo.common.exception.store

import com.eatngo.common.constant.StoreEnum
import com.eatngo.common.error.BusinessErrorCode
import com.eatngo.common.exception.BusinessException
import org.slf4j.event.Level

/**
 * 매장 관련 예외
 */
open class StoreException(
    override val errorCode: BusinessErrorCode,
    override val message: String = errorCode.message,
    override val data: Map<String, Any>? = null,
    override val logLevel: Level = Level.WARN,
    override val cause: Throwable? = null
) : BusinessException(errorCode, message, data, logLevel, cause) {

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
        message = "StoreOwnerId: $storeOwnerId 의 ${BusinessErrorCode.STORE_NOT_FOUND.message}",
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

    class StoreStatusUpdateFailed(storeId: Long, status: StoreEnum.StoreStatus) : StoreException(
        errorCode = BusinessErrorCode.STORE_UPDATE_FAILED,
        message = "매장 상태 업데이트 실패: 매장 ID $storeId, 상태 $status",
        data = mapOf("storeId" to storeId, "status" to status)
    )

    class StoreBatchUpdateFailed(
        expectedCount: Int,
        actualCount: Int,
        storeIds: List<Long>
    ) : StoreException(
        errorCode = BusinessErrorCode.STORE_BATCH_UPDATE_FAILED,
        message = "${BusinessErrorCode.STORE_BATCH_UPDATE_FAILED.message} (예상: ${expectedCount}개, 실제: ${actualCount}개)",
        data = mapOf(
            "expectedCount" to expectedCount,
            "actualCount" to actualCount,
            "storeIds" to storeIds
        ),
        logLevel = Level.ERROR
    )

    class StoreTotalStockException(
        storeId: Long,
        cause: Throwable? = null
    ) : StoreException(
        errorCode = BusinessErrorCode.STORE_TOTAL_STOCK_QUERY_FAILED,
        message = "FallBack 로직으로 매장 DB 총 재고 조회 실패: storeId=$storeId",
        data = mapOf("storeId" to storeId),
        cause = cause,
        logLevel = Level.WARN
    )

    class StoreCannotOpenNoStock(
        storeId: Long,
        totalStock: Int
    ) : StoreException(
        errorCode = BusinessErrorCode.STORE_CANNOT_OPEN_NO_STOCK,
        message = "${BusinessErrorCode.STORE_CANNOT_OPEN_NO_STOCK.message}: storeId=$storeId, totalStock=$totalStock",
        data = mapOf("storeId" to storeId, "totalStock" to totalStock),
        logLevel = Level.WARN
    )

    class StoreOwnerLimitExceeded(
        storeOwnerId: Long,
        currentStoreCount: Int,
        maxAllowedStores: Int
    ) : StoreException(
        errorCode = BusinessErrorCode.STORE_OWNER_LIMIT_EXCEEDED,
        message = "${BusinessErrorCode.STORE_OWNER_LIMIT_EXCEEDED.message} (최대 ${maxAllowedStores}개, 현재 ${currentStoreCount}개): storeOwnerId=$storeOwnerId",
        data = mapOf(
            "storeOwnerId" to storeOwnerId, 
            "currentStoreCount" to currentStoreCount,
            "maxAllowedStores" to maxAllowedStores
        ),
        logLevel = Level.WARN
    )
}