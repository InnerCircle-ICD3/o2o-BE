package com.eatngo.customer.rdb.repository

import com.eatngo.customer.rdb.entity.CustomerJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface CustomerRdbRepository : JpaRepository<CustomerJpaEntity, Long> {
    fun findByAccount_Id(userId: Long): CustomerJpaEntity?


    @Query(
        """
            SELECT c FROM CustomerJpaEntity c
            WHERE c.id = :id
        """
    )
    override fun findById(id: Long): Optional<CustomerJpaEntity>

}