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
    fun getAddressList(customerId: Long): Set<CustomerAddressDto> {
        val redisKey = customerAddressRedisRepository.getKey(customerId)
        val cachedAddress = customerAddressRedisRepository.findCustomerAddress(redisKey)

        return if (cachedAddress.isNotEmpty()) {
            cachedAddress
        } else {
            val customer = customerPersistence.findById(customerId)
                .orThrow { CustomerException.CustomerNotFound(customerId) }

            val addresses = customerAddressPersistence.findByCustomer(customer)

            val dtoSet = addresses.map { CustomerAddressDto.from(it) }.toMutableSet()

            customerAddressRedisRepository.save(
                key = redisKey,
                addresses = dtoSet
            )
            dtoSet
        }
    }


    fun addAddress(customerId: Long, addressCreateDto: AddressCreateDto): Long =
        customerPersistence.findById(customerId)
            .orThrow { CustomerException.CustomerNotFound(customerId) }
            .let { customer ->
                val savedAddress = customerAddressPersistence.save(customer, addressCreateDto.toCustomerAddress())

                val redisKey = customerAddressRedisRepository.getKey(savedAddress.customerId!!)
                customerAddressRedisRepository.save(
                    key = redisKey,
                    addresses = mutableSetOf(CustomerAddressDto.from(savedAddress))
                )
                savedAddress.id!!
            }

    fun deleteAddress(customerId: Long, id: Long): Unit =
        customerPersistence.findById(customerId)
            .orThrow { CustomerException.CustomerNotFound(customerId) }
            .let {
                customerAddressPersistence.deleteById(id)
            }
            .also {
                val key = customerAddressRedisRepository.getKey(customerId)
                customerAddressRedisRepository.deleteValue(key)
            }
}
