package com.eatngo.store_owner.service

import com.eatngo.common.exception.StoreException
import com.eatngo.store_owner.domain.StoreOwner
import com.eatngo.store_owner.dto.StoreOwnerUpdateDto
import com.eatngo.store_owner.infra.StoreOwnerPersistence
import com.eatngo.user_account.domain.UserAccount
import com.eatngo.user_account.infra.UserAccountPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class StoreOwnerService(
    private val userAccountPersistence: UserAccountPersistence,
    private val storeOwnerPersistence: StoreOwnerPersistence
) {

    fun getStoreOwnerById(id: Long): StoreOwner {
        return storeOwnerPersistence.findById(id) ?: throw StoreException.StoreNotFound(id)
    }

    @Transactional
    fun deleteStoreOwner(id: Long) {
        storeOwnerPersistence.deleteById(id)
        userAccountPersistence.deleteById(id)
    }

    @Transactional
    fun update(StoreOwnerId: Long, StoreOwnerUpdateDto: StoreOwnerUpdateDto) {
        val storeOwner = storeOwnerPersistence.getByIdOrThrow(StoreOwnerId)
        storeOwner.update(StoreOwnerUpdateDto)
        storeOwnerPersistence.save(storeOwner)

        val account = userAccountPersistence.getByIdOrThrow(storeOwner.account.id)
        account.update(StoreOwnerUpdateDto)
        userAccountPersistence.save(account)
    }

    @Transactional
    fun createByAccount(userAccount: UserAccount): StoreOwner {
        return storeOwnerPersistence.save(
            StoreOwner.create(userAccount)
        )
    }
}