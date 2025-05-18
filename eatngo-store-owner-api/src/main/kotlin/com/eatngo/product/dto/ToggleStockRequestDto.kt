package com.eatngo.product.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ToggleStockRequestDto(

    @field:NotNull(message = "상품 id는 필수입니다.")
    val id: Long,

    @field:NotBlank(message = "재고의 증감을 선택해주세요.")
    val action: String,

    val amount: Int = 1
)