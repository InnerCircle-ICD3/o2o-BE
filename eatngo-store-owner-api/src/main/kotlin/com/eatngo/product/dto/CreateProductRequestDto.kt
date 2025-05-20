package com.eatngo.product.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero

data class CreateProductRequestDto(
    @field:NotBlank(message = "상품명은 필수입니다")
    val name: String,

    @field:NotBlank(message = "상품 설명은 필수입니다")
    val description: String,

    @field:Positive(message = "원가는 양수여야 합니다")
    val originalPrice: Int,

    @field:NotBlank(message = "상품 사이즈는 필수입니다")
    val size: String,

    @field:NotBlank(message = "이미지 URL은 필수입니다")
    val image: String,

    @field:PositiveOrZero(message = "수량은 0 이상이어야 합니다")
    val quantity: Int,

    @field:NotEmpty(message = "음식 유형은 최소 하나 이상 선택해야 합니다")
    val foodType: List<String>
)