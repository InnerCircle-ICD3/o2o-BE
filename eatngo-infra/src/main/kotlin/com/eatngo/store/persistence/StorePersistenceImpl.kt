package com.eatngo.store.persistence

import com.eatngo.extension.mapOrNull
import com.eatngo.store.domain.Store
import com.eatngo.store.infra.StorePersistence
import com.eatngo.store.rdb.entity.StoreJpaEntity
import com.eatngo.store.rdb.repository.StoreRdbRepository
import org.springframework.stereotype.Component

/**
 * 매장 영속성 구현체
 */
@Component
class StorePersistenceImpl(
    private val storeRdbRepository: StoreRdbRepository
) : StorePersistence {
    
    override fun findById(id: Long): Store? =
        storeRdbRepository
            .findById(id)
            .mapOrNull(StoreJpaEntity::toStore)

    override fun findAllByIds(storeIds: List<Long>): List<Store> =
        storeRdbRepository.findAllByIds(storeIds)
            .map { StoreJpaEntity.toStore(it) }

    override fun findByOwnerId(storeOwnerId: Long): List<Store> =
        storeRdbRepository.findByStoreOwnerId(storeOwnerId)
            .map { StoreJpaEntity.toStore(it) }

    override fun save(store: Store): Store =
        StoreJpaEntity.toStore(
            storeRdbRepository.save(
                StoreJpaEntity.from(store)
            )
        )

    override fun deleteById(id: Long): Boolean =
        storeRdbRepository.softDeleteById(id) > 0

}