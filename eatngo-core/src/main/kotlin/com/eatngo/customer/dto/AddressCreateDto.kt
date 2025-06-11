package com.eatngo.customer.dto

import com.eatngo.common.type.Address
import com.eatngo.common.type.CoordinateVO
import com.eatngo.customer.domain.CustomerAddress
import com.eatngo.customer.domain.CustomerAddressType
import com.eatngo.store.dto.AddressDto
import com.eatngo.store.vo.LotNumberAddressVO
import com.eatngo.store.vo.RoadNameAddressVO
import com.eatngo.store.vo.ZipCodeVO

data class AddressCreateDto(
    val address: AddressDto,
    val radiusInKilometers: Double,  // 주소 반경 (단위: km)
    val customerAddressType: CustomerAddressType = CustomerAddressType.OTHER, // 주소 타입 (집, 회사, 기타)
    val description: String?, // 주소 설명
) {
    fun toCustomerAddress(): CustomerAddress = with(address) {
        return CustomerAddress(
            address = Address(
                roadNameAddress = roadNameAddress?.let { RoadNameAddressVO.from(it) },
                lotNumberAddress = LotNumberAddressVO.from(lotNumberAddress),
                buildingName = buildingName,
                zipCode = ZipCodeVO.from(zipCode),
                region1DepthName = region1DepthName,
                region2DepthName = region2DepthName,
                region3DepthName = region3DepthName,
                coordinate = CoordinateVO.from(coordinate.latitude!!, coordinate.longitude!!)
            ),
            radiusInKilometers = radiusInKilometers,
            customerAddressType = customerAddressType,
            description = description
        )
    }
}
