package com.eatngo.customer.domain

import com.eatngo.common.type.Address
import com.eatngo.common.type.CoordinateVO
import com.eatngo.customer.dto.CustomerAddressDto

class CustomerAddress(
    val id: Long? = null, // 주소 ID
    val radiusInKilometers: Double,  // 주소 반경 (단위: km)
    val customerId: Long? = null, // 고객 ID
    val address: Address, // 주소 정보
    val customerAddressType: CustomerAddressType = CustomerAddressType.OTHER, // 주소 타입 (집, 회사, 기타)
    val description: String?, // 주소 설명
) {
    companion object {
        fun create(customerAddressDto: CustomerAddressDto): CustomerAddress =
            CustomerAddress(
                id = customerAddressDto.id,
                customerId = customerAddressDto.customerId,
                radiusInKilometers = customerAddressDto.radiusInKilometers,
                address = Address(
                    roadNameAddress = customerAddressDto.roadNameAddress,
                    lotNumberAddress = customerAddressDto.lotNumberAddress,
                    buildingName = customerAddressDto.buildingName,
                    zipCode = customerAddressDto.zipCode,
                    region1DepthName = customerAddressDto.region1DepthName,
                    region2DepthName = customerAddressDto.region2DepthName,
                    region3DepthName = customerAddressDto.region3DepthName,
                    coordinate = CoordinateVO.from(customerAddressDto.latitude, customerAddressDto.longitude)
                ),
                customerAddressType = customerAddressDto.customerAddressType,
                description = customerAddressDto.description
            )
    }
}
