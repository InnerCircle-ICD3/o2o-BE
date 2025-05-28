package com.eatngo.customer.service

import com.eatngo.common.exception.CustomerException
import com.eatngo.customer.dto.AddressCreateDto
import com.eatngo.customer.dto.CustomerAddressDto
import com.eatngo.customer.infra.CustomerAddressPersistence
import com.eatngo.customer.infra.CustomerAddressRedisRepository
import com.eatngo.customer.infra.CustomerPersistence
import com.eatngo.extension.orThrow
import org.springframework.stereotype.Service

@Service
class CustomerAddressService(
    private val customerPersistence: CustomerPersistence,
    private val customerAddressPersistence: CustomerAddressPersistence,
    private val customerAddressRedisRepository: CustomerAddressRedisRepository,
) {
    fun getAddressList(customerId: Long): List<CustomerAddressDto> = customerPersistence.findById(customerId)
        .orThrow { CustomerException.CustomerNotFound(customerId) }
        .let {
            val customer = it
            customerAddressRedisRepository.findCustomerAddress(customerAddressRedisRepository.getKey(it.id))
                .ifEmpty { customerAddressPersistence.findByCustomer(customer) }
                .map(CustomerAddressDto.Companion::from)
        }

    fun addAddress(customerId: Long, addressCreateDto: AddressCreateDto): Long = customerPersistence.findById(customerId)
        .orThrow { CustomerException.CustomerNotFound(customerId) }
        .let {
            customerAddressPersistence.save(it, addressCreateDto.toCustomerAddress())
                .let {
                    val redisKey = customerAddressRedisRepository.getKey(it.customerId!!)
                    customerAddressRedisRepository.save(
                        key = redisKey,
                        address = it,
                    )
                    it.addressId!!
                }
        }

    fun deleteAddress(customerId: Long, addressId: Long) =
        customerPersistence.findById(customerId)
            .orThrow { CustomerException.CustomerNotFound(customerId) }
            .let {
                customerAddressPersistence.deleteById(addressId)
            }
            .also {
                val key = customerAddressRedisRepository.getKey(customerId)
                customerAddressRedisRepository.deleteValue(key)
            }
}
