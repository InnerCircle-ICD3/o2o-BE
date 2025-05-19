package com.eatngo.customer.domain

import com.eatngo.common.type.Point

class CustomerAddress (
    val addressId: Long,        // 주소 ID
    val customerId: Long,       // 고객 ID
    val point: Point,    // 위경도
    val address: String, // 주소 한글명(도로명주소)
    val addressType: AddressType = AddressType.ROAD, // 주소 타입 (도로명, 법정동, 행정동) => MVP 에서는 도로명 고정
    val customerAddressType: CustomerAddressType = CustomerAddressType.OTHER, // 주소 타입 (집, 회사, 기타)
    val addressTypeDesc: String?, // 주소 설명
) {
    companion object {
        fun create(): CustomerAddress {
            return CustomerAddress(
                addressId = 0L,
                customerId = 0L,
                point = Point(0.0, 0.0),
                address = "",
                addressType = AddressType.ROAD,
                customerAddressType = CustomerAddressType.OTHER,
                addressTypeDesc = ""
            )
        }
    }
}
