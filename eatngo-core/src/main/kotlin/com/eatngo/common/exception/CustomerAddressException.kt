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

    class CustomerAddressAlreadyExists(id: Long, roadNameAddressVO: RoadNameAddressVO, lotNumberAddress: LotNumberAddressVO) :
        CustomerAddressException(
            BusinessErrorCode.CUSTOMER_ADDRESS_ALREADY_EXISTS,
            "${BusinessErrorCode.CUSTOMER_ADDRESS_ALREADY_EXISTS.message} (ID: $id, Road Name: $roadNameAddressVO, Lot Number: $lotNumberAddress)",
            mapOf(
                "addressId" to id,
                "roadNameAddress" to roadNameAddressVO,
                "lotNumberAddress" to lotNumberAddress
            )
        )

}