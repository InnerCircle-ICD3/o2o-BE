package com.eatngo.customer.domain

import com.eatngo.common.type.CoordinateVO

class CustomerAddress(
    val addressId: Long, // 주소 ID
    val customerId: Long, // 고객 ID
    val coordinate: CoordinateVO, // 위경도
    val fullAddress: String, // 주소 한글명(도로명주소)
    val customerAddressType: CustomerAddressType = CustomerAddressType.OTHER, // 주소 타입 (집, 회사, 기타)
    val addressTypeDesc: String?, // 주소 설명
) {
    companion object {
        fun create(): CustomerAddress =
            CustomerAddress(
                addressId = 0L,
                customerId = 0L,
                coordinate = CoordinateVO.from(0.0, 0.0),
                fullAddress = "",
                customerAddressType = CustomerAddressType.OTHER,
                addressTypeDesc = "",
            )
    }
}
