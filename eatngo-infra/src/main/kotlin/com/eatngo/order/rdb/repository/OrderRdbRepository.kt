package com.eatngo.order.rdb.repository

import com.eatngo.order.domain.Status
import com.eatngo.order.rdb.entity.OrderJpaEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface OrderRdbRepository : JpaRepository<OrderJpaEntity, Long> {
    @Query("SELECT o FROM OrderJpaEntity o WHERE o.id = :id")
    override fun findById(id: Long): Optional<OrderJpaEntity>

    @Query(
        """
      SELECT o
      FROM OrderJpaEntity o
      JOIN OrderItemJpaEntity oi
      WHERE 1=1
        AND (o.status = :status)
        AND (:customerId IS NULL OR o.customerId = :customerId)
        AND (:storeId IS NULL OR o.storeId = :storeId)
        AND (:lastId IS NULL OR o.id < :lastId)
      ORDER BY o.id DESC
    """
    )
    fun cursoredFindAllByStatus(
        status: Status?,
        customerId: Long?,
        storeId: Long?,
        lastId: Long?,
        pageable: Pageable
    ): Slice<OrderJpaEntity>
}