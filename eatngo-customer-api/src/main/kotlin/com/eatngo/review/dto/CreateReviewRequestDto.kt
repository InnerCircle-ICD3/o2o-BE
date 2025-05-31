package com.eatngo.review.dto

data class CreateReviewRequestDto(
    val content: String,
    val images: List<String>,
    val score: Int
)