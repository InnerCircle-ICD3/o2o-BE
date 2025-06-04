package com.eatngo.review.rdb.repository

import com.eatngo.review.rdb.entity.ReviewJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ReviewRdbRepository : JpaRepository<ReviewJpaEntity, Long> {
    @Query("SELECT r FROM ReviewJpaEntity r WHERE r.orderId = :orderId")
    fun existsByOrderId(orderId: Long): Boolean
}