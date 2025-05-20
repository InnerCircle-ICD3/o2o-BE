package com.eatngo.customer.infra

import com.eatngo.customer.domain.Customer

interface CustomerPersistence {
    fun save(customer: Customer): Customer
    fun getByIdOrThrow(id: Long): Customer {
        return findById(id) ?: throw IllegalArgumentException("Customer not found")
    }

    fun findById(id: Long): Customer?
    fun deleteById(id: Long)
    fun findByUserId(userId: Long): (Customer)?
}