package com.eatngo.store_owner.service

import com.eatngo.store_owner.domain.StoreOwner
import com.eatngo.store_owner.infra.StoreOwnerPersistence
import com.eatngo.user_account.domain.UserAccount
import org.springframework.stereotype.Service

@Service
class StoreOwnerService(
    private val storeOwnerPersistence: StoreOwnerPersistence
) {
    fun createStoreOwner(account: UserAccount): StoreOwner {
        return storeOwnerPersistence.save(
            StoreOwner.create(
                account = account,
            )
        )
    }

    fun getStoreOwnerById(id: Long): StoreOwner? {
        return storeOwnerPersistence.getByIdOrThrow(id)
    }

    fun deleteStoreOwner(id: Long) {
        storeOwnerPersistence.deleteById(id)
    }
}