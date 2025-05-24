package com.eatngo.customer.service

import com.eatngo.customer.domain.CustomerAddress
import com.eatngo.customer.dto.CustomerAddressDto
import com.eatngo.customer.infra.CustomerAddressPersistence
import com.eatngo.customer.infra.CustomerAddressRedisRepository
import org.springframework.stereotype.Service

@Service
class CustomerAddressService(
    private val customerAddressPersistence: CustomerAddressPersistence,
    private val customerAddressRedisRepository: CustomerAddressRedisRepository,
) {
    fun getAddressList(): List<CustomerAddressDto> {
        // TODO : CustomerId 파라미터로 받아서 처리
        val customerId = 1L

        val redisKey = customerAddressRedisRepository.getKey(customerId)
        var addressList = customerAddressRedisRepository.findCustomerAddress(redisKey)
        if (addressList.isEmpty()) {
            addressList = customerAddressPersistence.findByCustomerId(customerId)
        }
        return addressList.map {
            CustomerAddressDto.from(it)
        }
    }

    fun addAddress(address: CustomerAddress): Long {
        // TODO : 주소 검증(위경도 <-> 주소 변환) 구현
        // TODO : 주소 중복 체크 구현
        val saveRes = customerAddressPersistence.save(address)

        val redisKey = customerAddressRedisRepository.getKey(saveRes.customerId)
        customerAddressRedisRepository.save(
            key = redisKey,
            address = saveRes,
        )
        return saveRes.addressId
    }

    fun deleteAddress(addressId: Long): Boolean {
        // TODO : CustomerId 파라미터로 받아서 처리
        val customerId = 1L

        val success = customerAddressPersistence.deleteById(addressId)
        if (success) {
            // TODO: 비동기로 처리?
            val key = customerAddressRedisRepository.getKey(customerId)
            customerAddressRedisRepository.deleteAddressId(key, addressId)
        }
        return success
    }
}
