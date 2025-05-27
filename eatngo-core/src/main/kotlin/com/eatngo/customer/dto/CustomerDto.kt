package com.eatngo.customer.dto

import com.eatngo.customer.domain.Customer
import java.time.LocalDateTime

data class CustomerDto(
    val id: Long,
    val userAccountId: Long,
    val nickname: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(customer: Customer): CustomerDto =
            CustomerDto(
                id = customer.id,
                userAccountId = customer.account.id,
                nickname = customer.account.nickname?.value,
                createdAt = customer.createdAt,
                updatedAt = customer.updatedAt
            )
    }
}

