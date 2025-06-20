package com.eatngo.customer.rdb.repository

import com.eatngo.common.type.CoordinateVO
import com.eatngo.customer.domain.CustomerAddressType
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

    @Query(
        """
            SELECT c FROM CustomerAddressJpaEntity c
            WHERE c.radiusInKilometers = :radiusInKilometers
            AND c.customerAddressType = :customerAddressType
            AND c.coordinate = :coordinate
            AND c.deletedAt IS NULL
        """
    )
    fun findByAddress(
        radiusInKilometers: Double,
        customerAddressType: CustomerAddressType,
        coordinate: CoordinateVO
    ): Optional<CustomerAddressJpaEntity>

}