package com.eatngo.store_owner.domain

import com.eatngo.user_account.domain.UserAccount
import java.time.ZonedDateTime

class StoreOwner(
    val id: Long = 0,
    val account: UserAccount,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime,
    val isDeleted: Boolean = false,
    val deletedAt: ZonedDateTime? = null,
) {
    companion object {
        fun create(account: UserAccount): StoreOwner {
            return StoreOwner(
                account = account,
                createdAt = ZonedDateTime.now(),
                updatedAt = ZonedDateTime.now()
            )
        }
    }
}