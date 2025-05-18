package com.eatngo.product.domain

enum class StockActionType(
    val value: String
) {
    INCREASE("increase"),
    DECREASE("decrease");

    companion object {
        fun fromValue(value: String): StockActionType =
            StockActionType.entries.firstOrNull() { it.value.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("$value 를 찾을 수 없습니다.")
    }
}