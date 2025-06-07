package com.eatngo.review.domain

@JvmInline
value class Score(val value: Int) {
    init{
        require(value >= MIN_SCORE) { "점수는 0점 이상이여야 합니다." }
        require(value <= MAX_SCORE) { "점수는 5점 이하이여야 합니다." }
    }

    companion object{
        private const val MAX_SCORE = 5
        private const val MIN_SCORE = 0
    }
}