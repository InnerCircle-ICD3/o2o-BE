package com.eatngo.common.exception.subscription

import com.eatngo.common.error.BusinessErrorCode
import com.eatngo.common.exception.BusinessException
import org.slf4j.event.Level

/**
 * 구독 관련 예외
 */
open class SubscriptionException(
    override val errorCode: BusinessErrorCode,
    override val message: String = errorCode.message,
    override val data: Map<String, Any>? = null,
    override val logLevel: Level = Level.WARN,
) : BusinessException(errorCode, message, data, logLevel) {

    class SubscriptionNotFound(subscriptionId: Long) : SubscriptionException(
        errorCode = BusinessErrorCode.SUBSCRIPTION_NOT_FOUND,
        message = "${BusinessErrorCode.SUBSCRIPTION_NOT_FOUND.message}: $subscriptionId",
        data = mapOf("subscriptionId" to subscriptionId)
    )

    class SubscriptionUpdateFailed(subscriptionId: Long) : SubscriptionException(
        errorCode = BusinessErrorCode.SUBSCRIPTION_UPDATE_FAILED,
        message = "${BusinessErrorCode.SUBSCRIPTION_UPDATE_FAILED.message}: $subscriptionId",
        data = mapOf("subscriptionId" to subscriptionId)
    )

    class SubscriptionForbidden(customerId: Long, subscriptionId: Long) : SubscriptionException(
        errorCode = BusinessErrorCode.SUBSCRIPTION_FORBIDDEN,
        message = "고객 ID: $customerId 는 구독 ID: $subscriptionId 에 대한 ${BusinessErrorCode.SUBSCRIPTION_FORBIDDEN.message}",
        data = mapOf("customerId" to customerId, "subscriptionId" to subscriptionId)
    )

    class SubscriptionAlreadyActive(subscriptionId: Long) : SubscriptionException(
        errorCode = BusinessErrorCode.SUBSCRIPTION_ALREADY_ACTIVE,
        message = "${BusinessErrorCode.SUBSCRIPTION_ALREADY_ACTIVE.message}: $subscriptionId",
        data = mapOf("subscriptionId" to subscriptionId)
    )

    class SubscriptionAlreadyCanceled(subscriptionId: Long) : SubscriptionException(
        errorCode = BusinessErrorCode.SUBSCRIPTION_ALREADY_CANCELED,
        message = "${BusinessErrorCode.SUBSCRIPTION_ALREADY_CANCELED.message}: $subscriptionId",
        data = mapOf("subscriptionId" to subscriptionId)
    )
}