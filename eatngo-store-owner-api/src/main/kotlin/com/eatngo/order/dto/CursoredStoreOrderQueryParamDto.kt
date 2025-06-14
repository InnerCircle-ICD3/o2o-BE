package com.eatngo.order.dto

import com.eatngo.order.domain.Status
import io.swagger.v3.oas.annotations.media.Schema

data class CursoredStoreOrderQueryParamDto(
    @Schema(description = "주문 상태", required = false, implementation = Status::class, example = "DONE")
    val status: Status?,
    @Schema(description = "마지막 주문 Id", required = false)
    val lastId: Long?,
) {
    companion object {
        fun toOrderQueryParamDto(
            storeId: Long,
            storeOwnerId: Long,
            dto: CursoredStoreOrderQueryParamDto,
        ) = StoreOrderQueryParamDto(
            storeId = storeId,
            customerId = null,
            status = dto.status,
            lastId = dto.lastId,
            storeOwnerId = storeOwnerId,
        )
    }
}
