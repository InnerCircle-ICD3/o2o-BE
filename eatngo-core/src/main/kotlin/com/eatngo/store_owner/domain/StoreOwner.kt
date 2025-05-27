package com.eatngo.store_owner.domain

import com.eatngo.store_owner.dto.StoreOwnerUpdateDto
import com.eatngo.user_account.domain.UserAccount
import java.time.LocalDateTime

class StoreOwner(
    val id: Long = 0,
    val account: UserAccount,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime,
    var deletedAt: LocalDateTime? = null,
) {
    fun update(storeOwnerUpdateDto: StoreOwnerUpdateDto) {
        TODO("Not yet implemented")
    }

    companion object {
        fun create(account: UserAccount): StoreOwner {
            return StoreOwner(
                account = account,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        }
    }

}