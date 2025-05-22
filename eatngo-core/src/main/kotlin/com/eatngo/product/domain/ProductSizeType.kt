package com.eatngo.product.domain

enum class ProductSizeType(
    val value: String
) {
    S("S"),
    M("M"),
    L("L");

    companion object {
        fun fromValue(value: String): ProductSizeType =
            entries.firstOrNull() { it.value.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("$value 를 찾을 수 없습니다.")
    }

}