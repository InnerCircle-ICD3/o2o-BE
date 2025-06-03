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

    // 매장 생성 검증 관련 예외
    class StoreValidationErrors(errorMessage: String, errors: Map<String, String>) : StoreException(
        BusinessErrorCode.STORE_VALIDATION_FAILED,
        errorMessage,
        mapOf("validationErrors" to errors)
    )

    class StoreNotFoundByStoreOwner(storeOwnerId: Long) : StoreException(
        BusinessErrorCode.STORE_NOT_FOUND,
        "${BusinessErrorCode.STORE_NOT_FOUND.message} (StoreOwnerId: $storeOwnerId)",
        mapOf("storeOwnerId" to storeOwnerId)
    )

    class InvalidAddressException(missingFields: Map<String, String>) : StoreException(
        BusinessErrorCode.STORE_VALIDATION_FAILED,
        "주소 정보가 유효하지 않습니다. 필수 필드: ${missingFields.keys.joinToString()}",
        mapOf("validationErrors" to missingFields)
    )

    // 매장 상태 관련 예외
    class StoreStatusInvalid(status: String) : StoreException(
        BusinessErrorCode.STORE_STATUS_INVALID,
        "${BusinessErrorCode.STORE_STATUS_INVALID.message}: $status",
        mapOf("status" to status)
    )

    // 매장 권한 관련 예외
    class Forbidden(storeOwnerId: Long) : StoreException(
        BusinessErrorCode.STORE_OWNER_FORBIDDEN,
        "점주아이디: $storeOwnerId 는 ${BusinessErrorCode.STORE_OWNER_FORBIDDEN.message}",
        mapOf("storeOwnerId" to storeOwnerId)
    )

    // 구독 관련 예외
    class SubscriptionNotFound(subscriptionId: Long) : StoreException(
        BusinessErrorCode.SUBSCRIPTION_NOT_FOUND,
        "${BusinessErrorCode.SUBSCRIPTION_NOT_FOUND.message}: $subscriptionId",
        mapOf("subscriptionId" to subscriptionId)
    )

    class SubscriptionUpdateFailed(subscriptionId: Long) : StoreException(
        BusinessErrorCode.SUBSCRIPTION_UPDATE_FAILED,
        "${BusinessErrorCode.SUBSCRIPTION_UPDATE_FAILED.message}: $subscriptionId",
        mapOf("subscriptionId" to subscriptionId)
    )

    // 추가: 구독 권한 관련 예외
    class SubscriptionForbidden(customerId: Long, subscriptionId: Long) : StoreException(
        BusinessErrorCode.SUBSCRIPTION_FORBIDDEN,
        "고객 ID: $customerId 는 구독 ID: $subscriptionId 에 대한 ${BusinessErrorCode.SUBSCRIPTION_FORBIDDEN.message}",
        mapOf("customerId" to customerId, "subscriptionId" to subscriptionId)
    )

    // 추가: 구독 상태 관련 예외
    class SubscriptionAlreadyActive(subscriptionId: Long) : StoreException(
        BusinessErrorCode.SUBSCRIPTION_ALREADY_ACTIVE,
        "${BusinessErrorCode.SUBSCRIPTION_ALREADY_ACTIVE.message}: $subscriptionId",
        mapOf("subscriptionId" to subscriptionId)
    )

    class SubscriptionAlreadyCanceled(subscriptionId: Long) : StoreException(
        BusinessErrorCode.SUBSCRIPTION_ALREADY_CANCELED,
        "${BusinessErrorCode.SUBSCRIPTION_ALREADY_CANCELED.message}: $subscriptionId",
        mapOf("subscriptionId" to subscriptionId)
    )
}