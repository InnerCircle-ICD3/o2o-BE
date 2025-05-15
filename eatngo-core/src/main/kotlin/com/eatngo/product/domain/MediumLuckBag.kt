package com.eatngo.product.domain

import java.time.ZonedDateTime

class MediumLuckBag (
    val id: Long = 0,
    val name: String,
    val description: String,
    val inventory: Inventory,
    val price: ProductPrice,
    val imageUrl: String,
    val storeId: Long,
    val foodTypes: FoodTypes,
    val status: ProductStatus,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime,
)