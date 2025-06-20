package com.eatngo.store_owner.service

import com.eatngo.common.exception.store.StoreException
import com.eatngo.customer.event.StoreOwnerDeletedEvent
import com.eatngo.store_owner.domain.StoreOwner
import com.eatngo.store_owner.dto.StoreOwnerUpdateDto
import com.eatngo.store_owner.infra.StoreOwnerPersistence
import com.eatngo.user_account.domain.UserAccount
import com.eatngo.user_account.infra.UserAccountPersistence
import org.springframework.stereotype.Service

@Service
class StoreOwnerService(
    private val userAccountPersistence: UserAccountPersistence,
    private val storeOwnerPersistence: StoreOwnerPersistence,
    private val applicationEventPublisher: org.springframework.context.ApplicationEventPublisher
) {

    fun getStoreOwnerById(id: Long): StoreOwner {
        return storeOwnerPersistence.findById(id) ?: throw StoreException.StoreNotFound(id)
    }

    fun deleteStoreOwner(id: Long) {
        val storeOwner = storeOwnerPersistence.getByIdOrThrow(id)
        storeOwnerPersistence.deleteById(id)
        applicationEventPublisher.publishEvent(
            StoreOwnerDeletedEvent(storeOwner.account.id)
        )
    }

    fun update(storeOwnerId: Long, storeOwnerUpdateDto: StoreOwnerUpdateDto) {
        val storeOwner = storeOwnerPersistence.getByIdOrThrow(storeOwnerId)
        storeOwner.update(storeOwnerUpdateDto)
        storeOwnerPersistence.save(storeOwner)

        val account = userAccountPersistence.getByIdOrThrow(storeOwner.account.id)
        account.update(storeOwnerUpdateDto)
        userAccountPersistence.save(account)
    }

    fun createByAccount(userAccount: UserAccount): StoreOwner {
        return storeOwnerPersistence.save(
            StoreOwner.create(userAccount)
        )
    }
}