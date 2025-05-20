package com.eatngo.store.persistence

import com.eatngo.store.domain.StoreSubscription
import com.eatngo.store.infra.StoreSubscriptionPersistence
import org.springframework.stereotype.Component

/**
 * 매장 구독 영속성 구현체
 */
@Component
class StoreSubscriptionPersistenceImpl : StoreSubscriptionPersistence {
    
    override suspend fun findById(id: Long): StoreSubscription? {
        TODO("Not yet implemented")
    }
    
    override suspend fun findByUserId(userId: Long): List<StoreSubscription> {
        TODO("Not yet implemented")
    }
    
    override suspend fun findByStoreId(storeId: Long): List<StoreSubscription> {
        TODO("Not yet implemented")
    }

    override suspend fun findByUserIdAndStoreId(userId: Long, storeId: Long): StoreSubscription?{
        TODO("Not yet implemented")
    }

    override suspend fun save(subscription: StoreSubscription): StoreSubscription {
        TODO("Not yet implemented")
    }
    
    override suspend fun softDelete(id: Long): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun existsByUserIdAndStoreId(userId: Long, storeId: Long): Boolean {
        TODO("Not yet implemented")
    }
}