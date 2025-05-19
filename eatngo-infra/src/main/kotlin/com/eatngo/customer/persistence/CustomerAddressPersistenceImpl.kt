package com.eatngo.customer.persistence

import com.eatngo.customer.domain.CustomerAddress
import com.eatngo.customer.infra.CustomerAddressPersistence
import org.springframework.stereotype.Component

@Component
class CustomerAddressPersistenceImpl : CustomerAddressPersistence {
    override fun save(customer: CustomerAddress): CustomerAddress {
        TODO("Not yet implemented")
    }

    override fun findByCustomerId(id: Long): List<CustomerAddress> {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Long) {
        TODO("Not yet implemented")
    }
}
