package com.eatngo.store_owner.dto

import com.eatngo.store_owner.domain.StoreOwner
import java.time.LocalDateTime

data class StoreOwnerDto(
    val id: Long,
    val userAccountId: Long,
    val nickname: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(storeOwner: StoreOwner): StoreOwnerDto =
            StoreOwnerDto(
                id = storeOwner.id,
                userAccountId = storeOwner.account.id,
                nickname = storeOwner.account.nickname,
                createdAt = storeOwner.createdAt,
                updatedAt = storeOwner.updatedAt
            )
    }
}

