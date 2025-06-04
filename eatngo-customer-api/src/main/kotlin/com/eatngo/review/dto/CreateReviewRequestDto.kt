package com.eatngo.review.dto

data class CreateReviewRequestDto(
    val content: String,
    val images: List<String>,
    val score: Int
) {
    companion object {
        fun toCreateReviewDto(dto: CreateReviewRequestDto, orderId: Long, customerId: Long) =
            CreateReviewDto(
                orderId = orderId,
                content = dto.content,
                images = dto.images,
                score = dto.score,
                customerId = customerId
            )
    }
}