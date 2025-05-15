package com.eatngo.product.dto

data class ProductCreateDto(
    val name: String,
    val description: String,
    val size: String,
    val inventory: ProductInventoryCreateDto,
    val price: ProductPriceCreateDto,
    val imageUrl: String?,
    val storeId: Long,
    val foodTypes: List<String>,
)

data class ProductInventoryCreateDto(
    val quantity: Int,
)

data class ProductPriceCreateDto(
    val originalPrice: Int,
)