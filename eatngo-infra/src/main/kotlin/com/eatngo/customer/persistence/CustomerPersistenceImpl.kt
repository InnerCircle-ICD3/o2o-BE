package com.eatngo.customer.persistence

import com.eatngo.customer.domain.Customer
import com.eatngo.customer.infra.CustomerPersistence
import com.eatngo.customer.rdb.entity.CustomerJpaEntity
import com.eatngo.customer.rdb.repository.CustomerRdbRepository
import org.springframework.stereotype.Component

@Component
class CustomerPersistenceImpl(
    private val customerRdbRepository: CustomerRdbRepository,
) : CustomerPersistence {
    override fun save(customer: Customer) =
        customerRdbRepository.save(CustomerJpaEntity.from(customer))
            .let { CustomerJpaEntity.toCustomer(it) }

    override fun findById(id: Long): Customer? {
        return customerRdbRepository.findById(id)
            .orElse(null)
            ?.let { CustomerJpaEntity.toCustomer(it) }
    }

    override fun deleteById(id: Long) {
        customerRdbRepository.softDeleteById(id)
    }

    override fun findByUserId(userId: Long): Customer? =
        customerRdbRepository.findByAccount_Id(userId)
            ?.let { customerJpaEntity ->
                return CustomerJpaEntity.toCustomer(customerJpaEntity)
            }
}