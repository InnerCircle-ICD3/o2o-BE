package com.eatngo.review.infra

import com.eatngo.common.response.Cursor
import com.eatngo.review.domain.Review

interface ReviewPersistence {
    fun save(review: Review): Review
    fun existsByOrderId(orderId: Long): Boolean
    fun findByOrderIds(orderIds: List<Long>): List<Review>
    fun findByStoreId(storeId: Long, lastId: Long?): Cursor<Review>
}