package com.eatngo.order.dto


import com.eatngo.order.domain.Status

data class CursoredStoreOrderQueryParamDto(
    val status: Status?,
    val lastId: Long?,
) {
    companion object {
        fun toOrderQueryParamDto(storeId: Long, storeOwnerId: Long, dto: CursoredStoreOrderQueryParamDto) = StoreOrderQueryParamDto(
            storeId = storeId,
            customerId = null,
            status = dto.status,
            lastId = dto.lastId,
            storeOwnerId = storeOwnerId
        )
    }
}
