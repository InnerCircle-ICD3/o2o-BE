package com.eatngo.review.domain

class Review(
    val id: Long = 0,
    val content: String,
    val score: Score,
    val images: Images
) {
}