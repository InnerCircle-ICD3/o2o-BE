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
            CustomerAddress.create()
        }
    }

    fun addAddress(address: CustomerAddress): Long {
        // TODO : 주소 등록 구현
        return 1L // 등록된 주소 ID 반환
    }

    fun deleteAddress(addressId: Long): Boolean {
        // TODO : 주소 삭제 구현
        return true
    }
}