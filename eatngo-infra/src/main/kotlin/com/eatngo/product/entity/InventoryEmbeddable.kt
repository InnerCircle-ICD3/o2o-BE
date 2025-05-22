package com.eatngo.product.entity

import jakarta.persistence.Embeddable

@Embeddable
data class InventoryEmbeddable(
    val quantity: Int,
    val stock: Int,
)