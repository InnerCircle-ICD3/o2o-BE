package com.eatngo.store_owner.domain

import com.eatngo.user_account.domain.UserAccount
import java.time.LocalDateTime

class StoreOwner(
    val id: Long = 0,
    val account: UserAccount,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime,
    var isDeleted: Boolean = false,
    var deletedAt: LocalDateTime? = null,
) {
    companion object {
        fun create(account: UserAccount): StoreOwner {
            return StoreOwner(
                account = account,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        }
    }

    fun delete() {
        if (isDeleted) {
            throw IllegalStateException("Store owner is already deleted")
        }
        isDeleted = true
        deletedAt = LocalDateTime.now()
    }
}