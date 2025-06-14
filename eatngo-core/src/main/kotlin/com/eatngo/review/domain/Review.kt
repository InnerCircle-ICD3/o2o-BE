package com.eatngo.review.domain

import com.eatngo.customer.domain.Customer
import java.time.LocalDateTime

class Review(
    val id: Long = 0,
    val orderId: Long,
    val content: String,
    val score: Score,
    val images: Images,
    val customerId: Long,
    val nickname: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val createdBy: Long? = null,
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val updatedBy: Long? = null,
    val deletedAt: LocalDateTime? = null,
) {
    fun canEditable(customer: Customer) = customer.id == customerId

    companion object {
        fun create(
            orderId: Long,
            content: String,
            score: Score,
            images: Images,
            customerId: Long,
            nickname: String,
        ) = Review(
            orderId = orderId,
            content = content,
            score = score,
            images = images,
            customerId = customerId,
            nickname = nickname,
        )
    }
}
