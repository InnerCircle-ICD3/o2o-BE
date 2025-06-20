package com.eatngo.review.rdb.repository

import com.eatngo.review.rdb.entity.ReviewJpaEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface ReviewRdbRepository : JpaRepository<ReviewJpaEntity, Long> {
    @Query("SELECT r FROM ReviewJpaEntity r WHERE r.orderId = :orderId")
    fun findByOrderId(orderId: Long): ReviewJpaEntity?

    @Query(
        "SELECT r FROM ReviewJpaEntity r WHERE r.orderId IN (:orderIds)",
    )
    fun findByOrderIds(orderIds: List<Long>): List<ReviewJpaEntity>

    @Query(
        """
      SELECT r
      FROM ReviewJpaEntity r
      JOIN OrderJpaEntity o ON o.id = r.orderId 
      WHERE 
        o.storeId = :storeId
        AND (:lastId IS NULL OR r.id < :lastId)
      ORDER BY r.id DESC
    """,
    )
    fun cursoredFindAllByStoreId(
        storeId: Long,
        lastId: Long?,
        pageable: Pageable,
    ): Slice<ReviewJpaEntity>

    @Query("SELECT r FROM ReviewJpaEntity r WHERE r.id = :id")
    override fun findById(id: Long): Optional<ReviewJpaEntity>
}
