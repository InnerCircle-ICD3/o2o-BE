package com.eatngo.customer.infra

import com.eatngo.customer.domain.CustomerAddress

interface CustomerAddressPersistence {
    fun save(customer: CustomerAddress): CustomerAddress

    fun findByCustomerId(customerId: Long): List<CustomerAddress>

    fun deleteById(id: Long)
}
