package com.eatngo.customer.infra

import com.eatngo.customer.domain.CustomerAddress

interface CustomerAddressRedisRepository {
    fun getKey(customerId: Long): String

    fun findCustomerAddress(key: String): List<CustomerAddress>

    fun save(
        key: String,
        address: CustomerAddress,
    )

    fun deleteValue(
        key: String,
    )
}
