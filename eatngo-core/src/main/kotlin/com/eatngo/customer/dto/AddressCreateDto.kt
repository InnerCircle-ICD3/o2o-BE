package com.eatngo.customer.dto

import com.eatngo.common.type.Address
import com.eatngo.customer.domain.CustomerAddress
import com.eatngo.customer.domain.CustomerAddressType

data class AddressCreateDto(
    val address: Address,
    val customerAddressType: CustomerAddressType = CustomerAddressType.OTHER, // 주소 타입 (집, 회사, 기타)
    val description: String?, // 주소 설명
) {
    fun toCustomerAddress(): CustomerAddress {
        return CustomerAddress(
            address = address,
            customerAddressType = customerAddressType,
            description = description
        )
    }
}
