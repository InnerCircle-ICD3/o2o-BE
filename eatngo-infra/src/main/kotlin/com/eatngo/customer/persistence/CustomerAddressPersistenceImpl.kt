package com.eatngo.customer.persistence

import com.eatngo.aop.SoftDeletedFilter
import com.eatngo.common.exception.user.CustomerAddressException
import com.eatngo.customer.domain.Customer
import com.eatngo.customer.domain.CustomerAddress
import com.eatngo.customer.infra.CustomerAddressPersistence
import com.eatngo.customer.rdb.entity.CustomerAddressJpaEntity
import com.eatngo.customer.rdb.entity.CustomerJpaEntity
import com.eatngo.customer.rdb.repository.CustomerAddressRdbRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class CustomerAddressPersistenceImpl(
    private val customerAddressRdbRepository: CustomerAddressRdbRepository,
) : CustomerAddressPersistence {
    override fun save(customer: Customer, customerAddress: CustomerAddress): CustomerAddress {
        val addressJpaEntity = CustomerAddressJpaEntity.of(
            customer = CustomerJpaEntity.from(customer),
            customerAddress = customerAddress
        )

        val address = customerAddress.address
        val existing = customerAddressRdbRepository.findByAddress(
            radiusInKilometers = customerAddress.radiusInKilometers,
            customerAddressType = customerAddress.customerAddressType,
            coordinate = address.coordinate
        )

        if (existing.isPresent) {
            throw CustomerAddressException.CustomerAddressAlreadyExists(
                customer.id,
                address.roadNameAddress?.value ?: "도로명 주소 없음",
                address.lotNumberAddress.value
            )
        }

        val saved = customerAddressRdbRepository.save(addressJpaEntity)
        return customerAddressRdbRepository.findById(saved.id)
            .orElseThrow { CustomerAddressException.CustomerAddressNotFound(saved.id) }
            .let { CustomerAddressJpaEntity.toCustomerAddress(it) }
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
