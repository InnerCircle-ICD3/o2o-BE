package com.eatngo.common.exception

import com.eatngo.common.error.BusinessErrorCode

open class CustomerAddressException(
    val errorCode: BusinessErrorCode,
    override val message: String = errorCode.message,
    val data: Map<String, Any>? = null,
) : RuntimeException(message) {

    class CustomerAddressNotFound(customerAddressId: Long) : CustomerAddressException(
        BusinessErrorCode.CUSTOMER_ADDRESS_NOT_FOUND,
        "${BusinessErrorCode.CUSTOMER_ADDRESS_NOT_FOUND.message} (ID: $customerAddressId)",
        mapOf("customerAddressId" to customerAddressId)
    )

}