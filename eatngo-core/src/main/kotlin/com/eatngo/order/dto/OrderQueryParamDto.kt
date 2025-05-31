package com.eatngo.order.dto

import com.eatngo.order.domain.Status


interface OrderQueryParamDto {
    val storeId: Long?
    val customerId: Long?
    val status: Status?
    val lastId: Long?
}

data class StoreOrderQueryParamDto(
    override val storeId: Long,
    override val customerId: Long? = null,
    override val status: Status? = null,
    override val lastId: Long? = null,
) : OrderQueryParamDto

data class CustomerOrderQueryParamDto(
    override val storeId: Long? = null,
    override val customerId: Long,
    override val status: Status? = null,
    override val lastId: Long? = null,
) : OrderQueryParamDto