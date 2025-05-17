package com.eatngo.product.entity

import jakarta.persistence.Embeddable

@Embeddable
data class ProductPriceEmbeddable(
    val originalPrice: Int,
    val discountRate: Double,
    val finalPrice: Int,
)