package com.eatngo.product.dto

data class UpdateProductRequestDto(
    val name: String,
    val description: String,
    val price: UpdateProductPriceRequestDto,
    val size: String,
    val inventory: UpdateProductInventoryRequestDto,
    val foodType: List<String>,
    val image: String?,
    val status: String?,
)

data class UpdateProductInventoryRequestDto(
    val quantity: Int,
    val stock: Int,
)

data class UpdateProductPriceRequestDto(
    val originalPrice: Int,
    val discountRate: Double,
    val finalPrice: Int
)