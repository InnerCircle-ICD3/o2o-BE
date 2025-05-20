package com.eatngo.store_owner.infra

import com.eatngo.store_owner.domain.StoreOwner

interface StoreOwnerPersistence {
    fun save(storeOwner: StoreOwner): StoreOwner
    fun getByIdOrThrow(id: Long): StoreOwner {
        return findById(id) ?: throw IllegalArgumentException("Store owner not found")
    }

    fun findById(id: Long): StoreOwner?
    fun deleteById(id: Long)
    fun findByUserId(userId: Long): StoreOwner?
}