package com.eatngo.review.dto

data class ReviewResponseDto(
    val id: Long,
    val content: String,
    val score: Int,
    val images: List<String>,
){
    companion object {
        fun from(reviewDto: ReviewDto) = with(reviewDto) {
            ReviewResponseDto(
                id = id,
                content = content,
                score = score,
                images = images,
            )
        }
    }
}

