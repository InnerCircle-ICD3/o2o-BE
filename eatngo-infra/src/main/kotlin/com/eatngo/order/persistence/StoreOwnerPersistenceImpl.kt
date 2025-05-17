package com.eatngo.order.persistence

import com.eatngo.store_owner.domain.StoreOwner
import com.eatngo.store_owner.infra.StoreOwnerPersistence
import org.springframework.stereotype.Component

@Component
class StoreOwnerPersistenceImpl: StoreOwnerPersistence {
    override fun save(storeOwner: StoreOwner): StoreOwner {
        TODO("Not yet implemented")
    }

    override fun findById(id: Long): StoreOwner? {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Long) {
        TODO("Not yet implemented")
    }
}