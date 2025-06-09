package com.eatngo.review.domain

import java.time.LocalDateTime

class Review(
    val id: Long = 0,
    val orderId: Long,
    val content: String,
    val score: Score,
    val images: Images,
    val customerId: Long,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val createdBy: Long? = null,
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val updatedBy: Long? = null,
    val deletedAt: LocalDateTime? = null
) {
    companion object {
        fun create(orderId: Long, content: String, score: Score, images: Images, customerId: Long) =
            Review(
                orderId = orderId,
                content = content,
                score = score,
                images = images,
                customerId = customerId,
            )
    }
}