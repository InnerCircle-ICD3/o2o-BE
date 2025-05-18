package com.eatngo.order.persistence

import com.eatngo.customer.domain.Customer
import com.eatngo.customer.infra.CustomerPersistence
import org.springframework.stereotype.Component

@Component
class CustomerPersistenceImpl : CustomerPersistence {
    override fun save(customer: Customer): Customer {
        TODO("Not yet implemented")
    }

    override fun findById(id: Long): Customer? {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Long) {
        TODO("Not yet implemented")
    }

}