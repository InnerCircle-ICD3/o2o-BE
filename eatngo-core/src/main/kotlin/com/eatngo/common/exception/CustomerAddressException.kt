package com.eatngo.common.exception

import com.eatngo.common.error.BusinessErrorCode
import com.eatngo.store.vo.LotNumberAddressVO
import com.eatngo.store.vo.RoadNameAddressVO
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