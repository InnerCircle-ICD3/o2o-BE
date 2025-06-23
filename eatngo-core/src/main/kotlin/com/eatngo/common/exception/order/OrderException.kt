package com.eatngo.common.exception.order

import com.eatngo.common.error.BusinessErrorCode
import com.eatngo.common.exception.BusinessException
import org.slf4j.event.Level

open class OrderException(
    override val errorCode: BusinessErrorCode,
    override val message: String = errorCode.message,
    override val data: Map<String, Any>? = null,
    override val logLevel: Level = Level.WARN,
) : BusinessException(errorCode, message, data, logLevel) {
    // 예외 생성
    class OrderNotFound(
        orderId: Long,
    ) : OrderException(
            errorCode = BusinessErrorCode.ORDER_NOT_FOUND,
            message = "${BusinessErrorCode.ORDER_NOT_FOUND.message} (ID: $orderId)",
            data = mapOf("orderId" to orderId),
        )

    class OrderAlreadyCompleted(
        orderId: Long,
    ) : OrderException(
            errorCode = BusinessErrorCode.ORDER_ALREADY_COMPLETED,
            message = "${BusinessErrorCode.ORDER_ALREADY_COMPLETED.message} (ID: $orderId)",
            data = mapOf("orderId" to orderId),
        )

    class OrderCreatedAlreadyExists(
        orderIds: List<Long>,
    ) : OrderException(
            errorCode = BusinessErrorCode.ORDER_CREATED_ALREADY_EXISTS,
            message = "${BusinessErrorCode.ORDER_CREATED_ALREADY_EXISTS.message} (ID: ${orderIds.joinToString(",")})",
            data = mapOf("orderIds" to orderIds),
        )
}
