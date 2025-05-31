package com.eatngo.order.dto

import com.eatngo.order.domain.Status

data class CursoredCustomerOrderQueryParamDto(
    val status: Status?,
    val lastId: Long?,
) {
    companion object {
        fun toOrderQueryParamDto(customerId: Long, dto: CursoredCustomerOrderQueryParamDto) =
            CustomerOrderQueryParamDto(
                storeId = null,
                customerId = customerId,
                status = dto.status,
                lastId = dto.lastId,
            )
    }
}
