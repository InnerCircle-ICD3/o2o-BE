package com.eatngo.customer.rdb.repository

import com.eatngo.customer.rdb.entity.CustomerAddressJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface CustomerAddressRdbRepository : JpaRepository<CustomerAddressJpaEntity, Long> {
    @Query(
        """
            SELECT c FROM CustomerAddressJpaEntity c
            WHERE c.id = :id
        """
    )
    override fun findById(id: Long): Optional<CustomerAddressJpaEntity>
    fun findByCustomerId(customerId: Long): MutableList<CustomerAddressJpaEntity>

}