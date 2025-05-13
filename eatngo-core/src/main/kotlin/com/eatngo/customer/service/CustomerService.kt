package com.eatngo.customer.service

import com.eatngo.customer.domain.Customer
import com.eatngo.customer.infra.CustomerPersistence
import com.eatngo.user_account.domain.UserAccount
import org.springframework.stereotype.Service

@Service
class CustomerService(
    private val customerPersistence: CustomerPersistence
) {
    fun createCustomer(account: UserAccount): Customer {
        return customerPersistence.save(
            Customer.create(
                account = account,
            )
        )
    }

    fun getCustomerById(id: Long): Customer {
        return customerPersistence.getByIdOrThrow(id)
    }

    fun deleteCustomer(id: Long) {
        customerPersistence.deleteById(id)
    }
}