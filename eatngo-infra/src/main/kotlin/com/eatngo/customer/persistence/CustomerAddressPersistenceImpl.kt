package com.eatngo.customer.persistence

import com.eatngo.aop.SoftDeletedFilter
import com.eatngo.common.exception.CustomerAddressException
import com.eatngo.customer.domain.Customer
import com.eatngo.customer.domain.CustomerAddress
import com.eatngo.customer.infra.CustomerAddressPersistence
import com.eatngo.customer.rdb.entity.CustomerAddressJpaEntity
import com.eatngo.customer.rdb.entity.CustomerJpaEntity
import com.eatngo.customer.rdb.repository.CustomerAddressRdbRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class CustomerAddressPersistenceImpl(
    private val customerAddressRdbRepository: CustomerAddressRdbRepository,
) : CustomerAddressPersistence {
    override fun save(customer: Customer, customerAddress: CustomerAddress): CustomerAddress {
        try {
            val customerAddressJpaEntity = customerAddressRdbRepository.save(
                CustomerAddressJpaEntity.of(
                    customer = CustomerJpaEntity.from(customer),
                    customerAddress = customerAddress
                )
            )
            return customerAddressRdbRepository.findById(customerAddressJpaEntity.id)
                .orElseThrow { CustomerAddressException.CustomerAddressNotFound(customerAddressJpaEntity.id) }
                .let { CustomerAddressJpaEntity.toCustomerAddress(it) }
        } catch (ViolationException: DataIntegrityViolationException) {
            throw CustomerAddressException.CustomerAddressAlreadyExists(
                customer.id, customerAddress.address.roadNameAddress,
                customerAddress.address.lotNumberAddress
            )
        }
    }

    @SoftDeletedFilter
    override fun findByCustomer(customer: Customer): List<CustomerAddress> {
        return customerAddressRdbRepository.findByCustomerId(customer.id)
            .map { CustomerAddressJpaEntity.toCustomerAddress(it) }
    }

    @SoftDeletedFilter
    override fun deleteById(customerAddressId: Long) {
        customerAddressRdbRepository.findById(customerAddressId)
            .orElseThrow { CustomerAddressException.CustomerAddressNotFound(customerAddressId) }
            .apply { delete() }
    }
}
