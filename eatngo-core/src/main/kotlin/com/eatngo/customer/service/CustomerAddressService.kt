package com.eatngo.customer.service

import com.eatngo.customer.domain.CustomerAddress
import com.eatngo.customer.dto.CustomerAddressDto
import com.eatngo.customer.infra.CustomerAddressPersistence
import org.springframework.stereotype.Service

@Service
class CustomerAddressService(
    private val customerAddressPersistence: CustomerAddressPersistence,
) {
    fun getAddressList(): List<CustomerAddressDto> {
        // TODO : Redis 캐시 조회
        // TODO : 캐시 미스 시 DB 조회
        val addressList = customerAddressPersistence.findByCustomerId(1L)
        return addressList.map {
            CustomerAddressDto.from(it)
        }
    }

    fun addAddress(address: CustomerAddress): Long {
        // TODO : 주소 검증(위경도 <-> 주소 변환) 구현
        // TODO : 주소 중복 체크 구현
        val saveRes = customerAddressPersistence.save(address)
        // TODO : 캐시 등록
        return saveRes.addressId
    }

    fun deleteAddress(addressId: Long): Boolean {
        // TODO : 캐시 삭제 구현
        customerAddressPersistence.deleteById(addressId)
        return true
    }
}
