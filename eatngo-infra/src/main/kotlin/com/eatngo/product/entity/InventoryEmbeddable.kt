package com.eatngo.product.entity

import jakarta.persistence.Embeddable

@Embeddable
data class InventoryEmbeddable(
    var quantity: Int,
    var stock: Int,
)