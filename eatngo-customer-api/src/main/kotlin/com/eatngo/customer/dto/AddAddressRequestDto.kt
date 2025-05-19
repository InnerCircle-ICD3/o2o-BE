package com.eatngo.customer.dto

import com.eatngo.common.type.Point
import com.eatngo.customer.domain.AddressType
import com.eatngo.customer.domain.CustomerAddressType

data class AddAddressRequestDto(
    val customerId: Long, // 고객 ID
    val point: Point,    // 위경도
    val address: String, // 주소 한글명(도로명주소)
    val addressType: AddressType= AddressType.ROAD, // 주소 타입 (도로명, 지번, 기타 등등)
    val customerAddressType: CustomerAddressType= CustomerAddressType.OTHER, // 주소 타입 (집, 회사, 기타)
    val addressTypeDesc: String?, // 주소 설명
)
