package com.eatngo.customer.service

import com.eatngo.common.exception.CustomerException
import com.eatngo.customer.domain.Customer
import com.eatngo.customer.dto.CustomerUpdateDto
import com.eatngo.customer.infra.CustomerPersistence
import com.eatngo.user_account.domain.UserAccount
import com.eatngo.user_account.infra.UserAccountPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CustomerService(
    private val userAccountPersistence: UserAccountPersistence,
    private val customerPersistence: CustomerPersistence,
) {


    fun getCustomerById(id: Long): Customer {
        return customerPersistence.findById(id) ?: throw CustomerException.CustomerNotFound(id)
    }

    @Transactional
    fun deleteCustomer(id: Long) {
        customerPersistence.deleteById(id)
        userAccountPersistence.deleteById(id)
    }

    @Transactional
    fun update(customerId: Long, customerUpdateDto: CustomerUpdateDto) {
        val customer = customerPersistence.getByIdOrThrow(customerId)
        customer.update(customerUpdateDto)
        customerPersistence.save(customer)

        val account = userAccountPersistence.getByIdOrThrow(customer.account.id)
        account.update(customerUpdateDto)
        userAccountPersistence.save(account)
    }

    @Transactional
    fun createByAccount(userAccount: UserAccount): Customer {
        return customerPersistence.save(
            Customer.create(userAccount)
        )
    }
}