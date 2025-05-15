package com.eatngo.customer.dto

import com.eatngo.common.type.Point
import com.eatngo.customer.domain.AddressType

data class AddAddressRequestDto(
    val address: String, // 주소 한글명
    val point: Point,    // 위경도
    val addressType: AddressType, // 주소 타입 (도로명, 지번, 기타 등등)
)
