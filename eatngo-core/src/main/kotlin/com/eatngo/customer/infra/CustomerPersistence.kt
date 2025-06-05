package com.eatngo.customer.infra

import com.eatngo.common.exception.user.CustomerException
import com.eatngo.customer.domain.Customer

interface CustomerPersistence {
    fun save(customer: Customer): Customer
    fun getByIdOrThrow(id: Long): Customer {
        return findById(id) ?: throw CustomerException.CustomerNotFound(id)
    }

    fun findById(id: Long): Customer?
    fun deleteById(id: Long)
    fun findByUserId(userId: Long): (Customer)?
}