package com.eatngo.customer.domain

import com.eatngo.common.type.Address
import com.eatngo.customer.dto.CustomerAddressDto

class CustomerAddress(
    val addressId: Long? = 0L, // 주소 ID
    val customerId: Long? = 0L, // 고객 ID
    val address: Address, // 주소 정보
    val customerAddressType: CustomerAddressType = CustomerAddressType.OTHER, // 주소 타입 (집, 회사, 기타)
    val description: String?, // 주소 설명
) {
    companion object {
        fun create(customerAddressDto: CustomerAddressDto): CustomerAddress =
            CustomerAddress(
                addressId = 0L, // 주소 ID는 생성 시 자동으로 할당되므로 0L로 초기화
                customerId = 0L, // 고객 ID는 나중에 설정할 수 있음
                address = customerAddressDto.address,
                customerAddressType = customerAddressDto.customerAddressType,
                description = customerAddressDto.description
            )
    }
}
