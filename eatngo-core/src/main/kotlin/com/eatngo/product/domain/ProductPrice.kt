package com.eatngo.product.domain

class ProductPrice(
    val originalPrice: Int,
    val discountRate: Double = 0.5,
    val finalPrice: Int = (originalPrice * (1 - discountRate)).toInt()
) {

    init {
        require(originalPrice > 0) { "상품 가격은 0보다 커야 합니다." }
        require(discountRate in 0.0..1.0) { "할인율은 0.0에서 1.0 사이여야 합니다." }
    }

    companion object {
        fun create(originalPrice: Int): ProductPrice {
            return ProductPrice(
                originalPrice = originalPrice,
            )
        }
    }
}