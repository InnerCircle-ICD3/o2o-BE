package com.eatngo.customer.domain

import com.eatngo.user_account.domain.UserAccount
import java.time.ZonedDateTime

class Customer(
    val id: Long = 0,
    val account: UserAccount,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime,
    val isDeleted: Boolean = false,
    val deletedAt: ZonedDateTime? = null,
) {
    companion object {
        fun create(account: UserAccount): Customer {
            return Customer(
                account = account,
                createdAt = ZonedDateTime.now(),
                updatedAt = ZonedDateTime.now()
            )
        }
    }
}