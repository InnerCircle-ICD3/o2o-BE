package com.eatngo.customer.dto

import com.eatngo.common.type.CoordinateVO
import com.eatngo.customer.domain.CustomerAddress
import com.eatngo.customer.domain.CustomerAddressType

/**
 * TODO: 현주님 코드 가져옴 -> 공통적으로 사용하는 Address는 common쪽으로 빼야 할듯?
 */
data class CustomerAddressDto(
    val roadAddress: RoadAddressDto, // 도로명 주소
    val legalAddress: LegalAddressDto?, // 법정동 주소
    val coordinate: CoordinateVO, // 위경도
    val customerAddressType: CustomerAddressType = CustomerAddressType.OTHER, // 주소 타입 (집, 회사, 기타)
    val addressTypeDesc: String?, // 주소 설명
) {
    companion object {
        fun from(customerAddress: CustomerAddress): CustomerAddressDto =
            CustomerAddressDto(
                roadAddress =
                    RoadAddressDto(
                        fullAddress = customerAddress.fullAddress,
                        zipCode = "", // TODO : 변환 필요
                    ),
                legalAddress = null, // TODO : 변환 필요
                coordinate = customerAddress.coordinate,
                customerAddressType = customerAddress.customerAddressType,
                addressTypeDesc = customerAddress.addressTypeDesc,
            )
    }
}

data class RoadAddressDto(
    val fullAddress: String,
    val zipCode: String,
)

data class LegalAddressDto(
    val fullAddress: String,
)
