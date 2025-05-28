package com.eatngo.store

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
    
    override fun findById(id: Long): Store? {
        TODO("Not yet implemented")
    }

    override fun findAllByIds(storeIds: List<Long>): List<Store> {
        TODO("Not yet implemented")
    }
    
    override fun findByOwnerId(storeOwnerId: Long): List<Store> {
        TODO("Not yet implemented")
    }

    override fun save(store: Store): Store {
        TODO("Not yet implemented")
    }
}