package com.eatngo.order.domain

@JvmInline
value class Score(val value: Int) {
    init{
        require(value >= MIN_SCORE) { "Score value can't smaller than {$MIN_SCORE}." }
        require(value <= MAX_SCORE) { "Score value can't bigger than {$MAX_SCORE}" }
    }

    companion object{
        private const val MAX_SCORE = 5
        private const val MIN_SCORE = 0
    }
}