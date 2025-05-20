package com.eatngo.product.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min

data class UpdateProductRequestDto(
    val name: String,
    val description: String,

    @field:Valid
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
    @field:Min(value = 0, message = "원가는 0 이상이어야 합니다")
    val originalPrice: Int,

    @field:DecimalMin(value = "0.0", message = "할인율은 0.0 이상이어야 합니다")
    @field:DecimalMax(value = "1.0", message = "할인율은 1.0 이하여야 합니다")
    val discountRate: Double,

    @field:Min(value = 0, message = "최종 가격은 0 이상이어야 합니다")
    val finalPrice: Int
)