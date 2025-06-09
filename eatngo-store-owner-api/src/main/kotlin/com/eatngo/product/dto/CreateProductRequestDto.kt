package com.eatngo.product.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min

data class CreateProductRequestDto(
    val name: String,
    val description: String,

    @field:Valid
    val price: UpdateProductPriceRequestDto,

    @field:Schema(
        description = "상품 사이즈",
        examples = ["L(large)", "M(medium)", "S(small)"],
    )
    val size: String,

    val inventory: UpdateProductInventoryRequestDto,

    @field:Schema(
        description = "잇고백 상품 세부 정보들",
        example = "단팥빵 ,소금빵, 팥빵"
    )
    val foodType: List<String>,

    val image: String?,

    @field:Schema(
        description = "상품 상태",
        examples = ["ACTIVE(판매중)", "INACTIVE(판매x)", "SOLD_OUT(재고 소진으로 인한 판매 종료)"]
    )
    val status: String?,
)

data class CreateProductInventoryRequestDto(
    @field:Schema(
        description = "점수가 설정한 초기 수량"
    )
    val quantity: Int,

    @field:Schema(
        description = "남아있는 상품의 현재 수량"
    )
    val stock: Int,
)

data class CreateProductPriceRequestDto(
    @field:Min(value = 0, message = "원가는 0 이상이어야 합니다")
    val originalPrice: Int,

    @field:DecimalMin(value = "0.0", message = "할인율은 0.0 이상이어야 합니다")
    @field:DecimalMax(value = "1.0", message = "할인율은 1.0 이하여야 합니다")
    @field:Schema(
        description = "상품의 할인률",
        example = "0.3",
        defaultValue = "0.5"
    )
    val discountRate: Double,
)