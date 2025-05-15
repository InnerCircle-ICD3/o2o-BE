package com.eatngo.product.dto

data class CreateProductRequestDto(
    val name: String,
    val description: String,
    val originalPrice: Int,
    val size: String,
    val quantity: Int,
    val foodType: List<String>
)