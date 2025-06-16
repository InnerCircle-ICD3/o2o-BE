package com.eatngo.order.dto

import com.eatngo.order.domain.Status
import io.swagger.v3.oas.annotations.media.Schema

data class CursoredCustomerOrderQueryParamDto(
    @Schema(description = "주문 상태", implementation = Status::class, example = "READY", nullable = true)
    val status: Status?,
    @Schema(description = "마지막 주문 Id", example = "50", nullable = true)
    val lastId: Long?,
) {
    companion object {
        fun toOrderQueryParamDto(
            customerId: Long,
            dto: CursoredCustomerOrderQueryParamDto,
        ) = CustomerOrderQueryParamDto(
            storeId = null,
            customerId = customerId,
            status = dto.status,
            lastId = dto.lastId,
        )
    }
}
