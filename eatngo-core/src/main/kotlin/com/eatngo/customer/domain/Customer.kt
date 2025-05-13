package com.eatngo.customer.domain

import com.eatngo.user_account.domain.UserAccount
import java.time.ZonedDateTime

class Customer(
    val id: Long = 0,
    val account: UserAccount,
    val createdAt: ZonedDateTime,
    var updatedAt: ZonedDateTime,
    var isDeleted: Boolean = false,
    var deletedAt: ZonedDateTime? = null,
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

    fun delete() {
        if (isDeleted) {
            throw IllegalStateException("Store owner is already deleted")
        }
        isDeleted = true
        deletedAt = ZonedDateTime.now()
    }
}