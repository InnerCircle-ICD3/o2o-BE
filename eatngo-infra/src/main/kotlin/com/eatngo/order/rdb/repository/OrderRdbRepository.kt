package com.eatngo.order.rdb.repository

import com.eatngo.order.rdb.entity.OrderJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRdbRepository: JpaRepository<OrderJpaEntity, Long> {
}