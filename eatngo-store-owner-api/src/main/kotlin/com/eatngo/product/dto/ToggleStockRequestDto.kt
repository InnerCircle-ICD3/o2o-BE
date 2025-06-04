package com.eatngo.product.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ToggleStockRequestDto(

    @field:NotNull(message = "상품 id는 필수입니다.")
    val id: Long,

    @field:NotBlank(message = "재고의 증감을 선택해주세요.")
    @field:Schema(
        description = "재고 증가 or 감소 선택",
        examples = ["increase", "decrease"]
    )
    val action: String,

    @field:Schema(
        description = "재고 증/감 수량",
        defaultValue = "1"
    )
    val amount: Int = 1
)