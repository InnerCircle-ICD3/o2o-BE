package com.eatngo.common.exception

import com.eatngo.common.error.BusinessErrorCode
import com.eatngo.store.vo.LotNumberAddressVO
import com.eatngo.store.vo.RoadNameAddressVO

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

    class CustomerAddressAlreadyExists(addressId: Long, id: RoadNameAddressVO, lotNumberAddress: LotNumberAddressVO) :
        CustomerAddressException(
            BusinessErrorCode.CUSTOMER_ADDRESS_ALREADY_EXISTS,
            "${BusinessErrorCode.CUSTOMER_ADDRESS_ALREADY_EXISTS.message} (ID: $addressId, Road Name: $id, Lot Number: $lotNumberAddress)",
            mapOf(
                "addressId" to addressId,
                "roadNameAddress" to id,
                "lotNumberAddress" to lotNumberAddress
            )
        )

}