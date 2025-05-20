package com.eatngo.customer.domain

import com.eatngo.user_account.domain.UserAccount
import java.time.LocalDateTime

class Customer(
    val id: Long = 0,
    val account: UserAccount,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime,
    var deletedAt: LocalDateTime? = null,
) {
    companion object {
        fun create(account: UserAccount): Customer {
            return Customer(
                account = account,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        }
    }

}