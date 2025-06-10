package com.eatngo.common.exception.user

import com.eatngo.common.error.BusinessErrorCode
import com.eatngo.common.exception.BusinessException
import org.slf4j.event.Level


open class CustomerAddressException(
    override val errorCode: BusinessErrorCode,
    override val message: String = errorCode.message,
    override val data: Map<String, Any>? = null,
    override val logLevel: Level = Level.WARN,
) : BusinessException(errorCode, message, data, logLevel) {

    class CustomerAddressNotFound(customerAddressId: Long) : CustomerAddressException(
        errorCode = BusinessErrorCode.CUSTOMER_ADDRESS_NOT_FOUND,
        message = "${BusinessErrorCode.CUSTOMER_ADDRESS_NOT_FOUND.message} (ID: $customerAddressId)",
        data = mapOf("customerAddressId" to customerAddressId)
    )

    class CustomerAddressAlreadyExists(id: Long, roadNameAddress: String, lotNumberAddress: String) :
        CustomerAddressException(
            BusinessErrorCode.CUSTOMER_ADDRESS_ALREADY_EXISTS,
            "${BusinessErrorCode.CUSTOMER_ADDRESS_ALREADY_EXISTS.message} (ID: $id, Road Name: $roadNameAddress, Lot Number: $lotNumberAddress)",
            mapOf(
                "addressId" to id,
                "roadNameAddress" to roadNameAddress,
                "lotNumberAddress" to lotNumberAddress
            )
        )

}