package com.eatngo.search.domain

import com.eatngo.product.domain.ProductStatus

enum class SearchProductStatus(
    val code: Int, // 상태 코드 -> 정렬을 위해 Activate를 1로...
) {
    ACTIVE(1),
    INACTIVE(2),
    SOLD_OUT(3),
    ;

    companion object {
        fun from(status: ProductStatus): SearchProductStatus =
            when (status) {
                ProductStatus.ACTIVE -> ACTIVE
                ProductStatus.INACTIVE -> INACTIVE
                ProductStatus.SOLD_OUT -> SOLD_OUT
            }

        fun from(code: Int): SearchProductStatus =
            when (code) {
                1 -> ACTIVE
                2 -> INACTIVE
                3 -> SOLD_OUT
                else -> throw IllegalArgumentException("Invalid status code: $code")
            }
    }
}
