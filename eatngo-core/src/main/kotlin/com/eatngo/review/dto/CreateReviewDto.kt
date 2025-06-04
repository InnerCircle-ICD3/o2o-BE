package com.eatngo.review.dto

data class CreateReviewDto(
    val orderId: Long,
    val content:String,
    val images:List<String>,
    val score: Int,
    val customerId: Long,
)