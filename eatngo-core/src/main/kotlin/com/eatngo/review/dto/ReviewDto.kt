package com.eatngo.review.dto

import com.eatngo.review.domain.Review

data class ReviewDto(
    val id: Long,
    val orderId: Long,
    val content: String,
    val images: List<String>,
    val score: Int
) {
    companion object {
        fun from(review: Review) = with(review) {
            ReviewDto(
                id = id,
                orderId = id,
                content = content,
                images = images.images,
                score = score.value
            )
        }
    }
}
