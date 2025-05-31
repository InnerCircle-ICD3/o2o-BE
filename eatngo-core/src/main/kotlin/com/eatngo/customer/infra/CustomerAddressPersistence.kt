package com.eatngo.customer.infra

import com.eatngo.customer.domain.Customer
import com.eatngo.customer.domain.CustomerAddress

interface CustomerAddressPersistence {
    fun save(customer: Customer, customerAddress: CustomerAddress): CustomerAddress

    fun findByCustomer(customer: Customer): List<CustomerAddress>

    fun deleteById(customerAddressId: Long)
}
