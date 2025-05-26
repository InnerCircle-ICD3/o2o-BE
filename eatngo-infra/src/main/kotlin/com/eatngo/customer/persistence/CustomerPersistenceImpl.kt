package com.eatngo.customer.persistence

import com.eatngo.aop.SoftDeletedFilter
import com.eatngo.customer.domain.Customer
import com.eatngo.customer.infra.CustomerPersistence
import com.eatngo.customer.rdb.entity.CustomerJpaEntity
import com.eatngo.customer.rdb.repository.CustomerRdbRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class CustomerPersistenceImpl(
    private val customerRdbRepository: CustomerRdbRepository,
) : CustomerPersistence {
    override fun save(customer: Customer): Customer {
        return customerRdbRepository.save(CustomerJpaEntity.from(customer))
            .let { CustomerJpaEntity.toCustomer(it) }
    }

    @SoftDeletedFilter
    override fun findById(id: Long): Customer? {
        return customerRdbRepository.findById(id)
            .orElse(null)
            ?.let { CustomerJpaEntity.toCustomer(it) }
    }

    @SoftDeletedFilter
    override fun deleteById(id: Long) {
        customerRdbRepository.findById(id).orElseThrow()
            .apply { delete() }
    }

    @SoftDeletedFilter
    override fun findByUserId(userId: Long): Customer? =
        customerRdbRepository.findByAccount_Id(userId)
            ?.let { customerJpaEntity ->
                return CustomerJpaEntity.toCustomer(customerJpaEntity)
            }
}