package com.eatngo.product.dto

import io.swagger.v3.oas.annotations.media.Schema
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
    @field:Schema(
        description = "상품 사이즈(large, medium, small)",
        examples = ["L", "M", "S"],
    )
    val size: String,

    @field:NotBlank(message = "이미지 URL은 필수입니다")
    @field:Schema(
        description = "이미지 url"
    )
    val image: String,

    @field:PositiveOrZero(message = "수량은 0 이상이어야 합니다")
    @field:Schema(
        description = "점주가 초기에 설정한 수량"
    )
    val quantity: Int,

    @field:NotEmpty(message = "음식 유형은 최소 하나 이상 선택해야 합니다")
    @field:Schema(
        description = "잇고백 상품 세부 정보들",
        example = "단팥빵 ,소금빵, 팥빵"
    )
    val foodType: List<String>
)