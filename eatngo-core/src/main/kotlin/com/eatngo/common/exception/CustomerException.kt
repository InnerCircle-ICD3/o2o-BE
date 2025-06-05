package com.eatngo.common.exception

import com.eatngo.common.error.BusinessErrorCode
import org.slf4j.event.Level

open class CustomerException(
    override val errorCode: BusinessErrorCode,
    override val message: String = errorCode.message,
    override val data: Map<String, Any>? = null,
    override val logLevel: Level = Level.WARN,
) : BusinessException(errorCode, message, data, logLevel) {

    class CustomerNotFound(customerId: Long) : CustomerException(
        errorCode =BusinessErrorCode.CUSTOMER_NOT_FOUND,
        message = "${BusinessErrorCode.CUSTOMER_NOT_FOUND.message} (ID: $customerId)",
        data = mapOf("customerId" to customerId)
    )

}