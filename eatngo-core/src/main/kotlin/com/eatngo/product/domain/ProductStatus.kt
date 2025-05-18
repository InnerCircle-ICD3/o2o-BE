package com.eatngo.product.domain

enum class ProductStatus(val value: String) {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    SOLD_OUT("SOLD_OUT");

    companion object {
        fun fromValue(value: String): ProductStatus =
            ProductStatus.entries.firstOrNull() { it.value.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("$value 를 찾을 수 없습니다.")
    }
}