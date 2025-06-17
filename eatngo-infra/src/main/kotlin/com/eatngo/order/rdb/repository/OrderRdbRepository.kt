package com.eatngo.order.rdb.repository

import com.eatngo.order.domain.Status
import com.eatngo.order.rdb.entity.OrderJpaEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime
import java.util.*

interface OrderRdbRepository : JpaRepository<OrderJpaEntity, Long> {
    @Query("SELECT o FROM OrderJpaEntity o WHERE o.id = :id")
    override fun findById(id: Long): Optional<OrderJpaEntity>

    @Query(
        """
      SELECT o
      FROM OrderJpaEntity o
      JOIN OrderItemJpaEntity oi ON o.id = oi.order.id
      WHERE 1=1
        AND (:status IS NULL OR o.status = :status)
        AND (:customerId IS NULL OR o.customerId = :customerId)
        AND (:storeId IS NULL OR o.storeId = :storeId)
        AND (:lastId IS NULL OR o.id < :lastId)
        AND (CAST(:updatedAt AS timestamp) IS NULL OR o.updatedAt <= CAST(:updatedAt AS timestamp))
      ORDER BY o.id DESC 
    """,
    )
    fun cursoredFindAllByStatus(
        status: Status?,
        customerId: Long?,
        storeId: Long?,
        lastId: Long?,
        updatedAt: LocalDateTime?,
        pageable: Pageable,
    ): Slice<OrderJpaEntity>
}
