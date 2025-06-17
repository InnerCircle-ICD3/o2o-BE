package com.eatngo.order.dto

import com.eatngo.order.domain.Status
import java.time.LocalDateTime

open class OrderQueryParamDto(
    open val storeId: Long?,
    open val customerId: Long?,
    open val status: Status?,
    open val lastId: Long?,
    open val updatedAt: LocalDateTime?,
)

data class StoreOrderQueryParamDto(
    override val storeId: Long,
    override val customerId: Long? = null,
    override val status: Status? = null,
    override val lastId: Long? = null,
    override val updatedAt: LocalDateTime? = null,
    val storeOwnerId: Long,
) : OrderQueryParamDto(
        storeId = storeId,
        customerId = customerId,
        status = status,
        lastId = lastId,
        updatedAt = updatedAt,
    )

data class CustomerOrderQueryParamDto(
    override val storeId: Long? = null,
    override val customerId: Long,
    override val status: Status? = null,
    override val lastId: Long? = null,
    override val updatedAt: LocalDateTime? = null,
) : OrderQueryParamDto(
        storeId = storeId,
        customerId = customerId,
        status = status,
        lastId = lastId,
        updatedAt = updatedAt,
    )
