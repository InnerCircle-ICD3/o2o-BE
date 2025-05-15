package com.eatngo.customer.service

import com.eatngo.common.type.Point
import com.eatngo.customer.domain.AddressType
import com.eatngo.customer.domain.CustomerAddress
import org.springframework.stereotype.Service

@Service
class CustomerAddressService {
    fun getAddressList(): List<CustomerAddress> {
        // TODO : 주소 조회 구현
        return List(1) {
            CustomerAddress(
                id = 1L,
                address = "서울특별시 강남구 테헤란로 123",
                point = Point(
                    lat = 37.123456,
                    lng = 127.123456
                ),
                addressType = AddressType.ROAD
            )
        }
    }

    fun addAddress(address: String): Long {
        // TODO : 주소 등록 구현
        return 1L
    }

    fun deleteAddress(addressId: Long): Boolean {
        // TODO : 주소 삭제 구현
        return true
    }
}