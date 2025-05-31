package com.eatngo.customer.infra

import com.eatngo.customer.dto.CustomerAddressDto

interface CustomerAddressRedisRepository {
    fun getKey(customerId: Long): String

    fun findCustomerAddress(key: String): MutableSet<CustomerAddressDto>

    fun save(
        key: String,
        addresses: MutableSet<CustomerAddressDto>,
    )

    fun deleteValue(
        key: String,
    )
}
