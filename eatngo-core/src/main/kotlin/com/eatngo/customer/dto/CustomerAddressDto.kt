package com.eatngo.customer.dto

import com.eatngo.customer.domain.CustomerAddress
import com.eatngo.customer.domain.CustomerAddressType
import com.eatngo.store.vo.LotNumberAddressVO
import com.eatngo.store.vo.RoadNameAddressVO
import com.eatngo.store.vo.ZipCodeVO

data class CustomerAddressDto(
    val id: Long, // 주소 ID
    val customerId: Long, // 고객 ID
    val roadNameAddress: RoadNameAddressVO? = null,     // 도로명 주소
    val lotNumberAddress: LotNumberAddressVO,   // 지번 주소
    val buildingName: String?,                  // 건물명
    val zipCode: ZipCodeVO,                     // 우편번호
    val region1DepthName: String?,              // 시도명
    val region2DepthName: String?,              // 시군구명
    val region3DepthName: String?,              // 상세주소
    val latitude: Double, // 위도
    val longitude: Double, // 경도
    val customerAddressType: CustomerAddressType = CustomerAddressType.OTHER, // 주소 타입 (집, 회사, 기타)
    val description: String?, // 주소 설명
) {
    companion object {
        fun from(customerAddress: CustomerAddress): CustomerAddressDto =
            with(customerAddress.address) {
                CustomerAddressDto(
                    id = customerAddress.id!!,
                    customerId = customerAddress.customerId!!,
                    roadNameAddress = roadNameAddress?.let { RoadNameAddressVO.from(it.value) },
                    lotNumberAddress = LotNumberAddressVO.from(lotNumberAddress.value),
                    buildingName = buildingName,
                    zipCode = ZipCodeVO.from(zipCode.value),
                    region1DepthName = region1DepthName,
                    region2DepthName = region2DepthName,
                    region3DepthName = region3DepthName,
                    latitude = coordinate.latitude,
                    longitude = coordinate.longitude,
                    customerAddressType = customerAddress.customerAddressType,
                    description = customerAddress.description
                )
            }
    }
}
