package com.eatngo.customer.dto

import com.eatngo.common.type.Coordinate
import com.eatngo.customer.domain.CustomerAddressType

data class AddAddressRequestDto(
    val customerId: Long, // 고객 ID
    val coordinate: Coordinate, // 위경도
    val fullAddress: String, // 주소 한글명(도로명주소)
    val customerAddressType: CustomerAddressType = CustomerAddressType.OTHER, // 주소 타입 (집, 회사, 기타)
    val addressTypeDesc: String?, // 주소 설명
)
