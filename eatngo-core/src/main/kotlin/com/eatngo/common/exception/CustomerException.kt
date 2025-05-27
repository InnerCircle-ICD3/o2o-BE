package com.eatngo.common.exception

import com.eatngo.common.error.BusinessErrorCode

open class CustomerException(
    val errorCode: BusinessErrorCode,
    override val message: String = errorCode.message,
    val data: Map<String, Any>? = null,
) : RuntimeException(message) {

    class CustomerNotFound(customerId: Long) : CustomerException(
        BusinessErrorCode.CUSTOMER_NOT_FOUND,
        "${BusinessErrorCode.CUSTOMER_NOT_FOUND.message} (ID: $customerId)",
        mapOf("customerId" to customerId)
    )

}