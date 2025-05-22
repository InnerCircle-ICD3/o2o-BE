package com.eatngo.common.exception

import com.eatngo.common.error.BusinessErrorCode

open class OrderException(
    val errorCode: BusinessErrorCode,
    override val message: String = errorCode.message,
    val data: Map<String, Any>? = null,
) : RuntimeException(message) {

    // 예외 생성
    class OrderNotFound(orderId: Long) : OrderException(
        BusinessErrorCode.ORDER_NOT_FOUND,
        "${BusinessErrorCode.ORDER_NOT_FOUND.message} (ID: $orderId)",
        mapOf("orderId" to orderId)
    )

    class OrderAlreadyCompleted(orderId: Long) : OrderException(
        BusinessErrorCode.ORDER_ALREADY_COMPLETED,
        "${BusinessErrorCode.ORDER_ALREADY_COMPLETED.message} (ID: $orderId)",
        mapOf("orderId" to orderId)
    )

    // 추후 작성
}