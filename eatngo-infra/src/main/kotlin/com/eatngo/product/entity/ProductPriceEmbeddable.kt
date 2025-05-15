package com.eatngo.product.entity

import jakarta.persistence.Embeddable

@Embeddable
data class ProductPriceEmbeddable(
    var originalPrice: Int,
    var discountRate: Double,
    var finalPrice: Int,
) {
}