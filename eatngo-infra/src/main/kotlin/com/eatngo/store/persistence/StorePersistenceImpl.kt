package com.eatngo.store.persistence

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.domain.Address
import com.eatngo.store.domain.Store
import com.eatngo.store.infra.StorePersistence
import org.springframework.stereotype.Component

/**
 * 매장 영속성 구현체
 */
@Component
class StorePersistenceImpl : StorePersistence {
    
    override suspend fun findById(id: Long): Store? {
        TODO("Not yet implemented")
    }

    override suspend fun findAllByIds(storeIds: List<Long>): List<Store> {
        TODO("Not yet implemented")
    }
    
    override suspend fun findByOwnerId(storeOwnerId: Long): List<Store> {
        TODO("Not yet implemented")
    }

    override suspend fun save(store: Store): Store {
        TODO("Not yet implemented")
    }

    override suspend fun softDelete(id: Long): Boolean {
        TODO("Not yet implemented")
    }
    
    override suspend fun updateStatus(id: Long, status: StoreEnum.StoreStatus): Boolean {
        TODO("Not yet implemented")
    }
}