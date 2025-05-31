package com.eatngo.order.domain

class Review(
    val id: Long = 0,
    val content: String,
    val score: Score,
) {
}