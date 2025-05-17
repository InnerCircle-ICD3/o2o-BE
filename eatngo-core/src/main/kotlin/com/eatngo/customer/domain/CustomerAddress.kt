package com.eatngo.customer.domain

import com.eatngo.common.type.Point

class CustomerAddress (
    val id: Long,        // 주소 ID
    val address: String, // 주소 한글명
    val point: Point,    // 위경도
    val addressType: AddressType, // 주소 타입 (도로명, 지번, 기타 등등)
) {
    companion object {
        fun create(): CustomerAddress {
            return CustomerAddress(
                id = 0L,
                address = "",
                point = Point(0.0, 0.0),
                addressType = AddressType.ROAD
            )
        }
    }
}
