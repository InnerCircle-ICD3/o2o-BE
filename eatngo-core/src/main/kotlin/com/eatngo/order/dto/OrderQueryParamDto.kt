package com.eatngo.order.dto

import com.eatngo.order.domain.Status


data class OrderQueryParamDto(
    val storeId: Long?,
    val customerId: Long?,
    val status: Status?,
    val lastId: Long?,
)