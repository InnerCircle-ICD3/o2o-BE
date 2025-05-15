package com.eatngo.product.domain

class ProductPrice(
    val originalPrice: Int,
    val discountRate: Double = 0.5,
    val finalPrice: Int = (originalPrice * (1 - discountRate)).toInt()
) {

    companion object {
        fun create(originalPrice: Int): ProductPrice {
            return ProductPrice(
                originalPrice = originalPrice,
            )
        }
    }
}