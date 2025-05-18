package com.eatngo.order.rdb.repository

import com.eatngo.order.rdb.entity.OrderJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface OrderRdbRepository : JpaRepository<OrderJpaEntity, Long> {

    @Query("SELECT o FROM OrderJpaEntity o WHERE o.id = :id")
    override fun findById(id: Long): Optional<OrderJpaEntity>
}