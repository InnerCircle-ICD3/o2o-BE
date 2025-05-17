package com.eatngo.store.persistence

import com.eatngo.store.constant.StoreEnum
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

    override suspend fun findAllByIds(storeIds: List<String>): List<Store> {
        TODO("Not yet implemented")
    }
    
    override suspend fun findByOwnerId(ownerId: String): Store? {
        TODO("Not yet implemented")
    }
    
    override suspend fun findNearby(address: Address, radiusKm: Double, limit: Int, offset: Int): List<Store> {
        TODO("Not yet implemented")
    }
    
    override suspend fun findByNameContaining(name: String, limit: Int, offset: Int): List<Store> {
        TODO("Not yet implemented")
    }
    
    override suspend fun findByCategory(category: String, limit: Int, offset: Int): List<Store> {
        TODO("Not yet implemented")
    }
    
    override suspend fun findOpenStores(limit: Int, offset: Int): List<Store> {
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
    
    // 거리 계산 헬퍼 메서드
    private fun calculateDistance(customerLocation: Address, storeLocation: Address): Double {
        TODO("Not yet")
    }
}