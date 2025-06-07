package com.eatngo.store.persistence

import com.eatngo.aop.SoftDeletedFilter
import com.eatngo.extension.mapOrNull
import com.eatngo.store.domain.Store
import com.eatngo.store.infra.StorePersistence
import com.eatngo.store.rdb.entity.StoreJpaEntity
import com.eatngo.store.rdb.repository.StoreRdbRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * 매장 영속성 구현체
 */
@Component
class StorePersistenceImpl(
    private val storeRdbRepository: StoreRdbRepository,
) : StorePersistence {
    @SoftDeletedFilter
    override fun findById(id: Long): Store? =
        storeRdbRepository
            .findById(id)
            .mapOrNull(StoreJpaEntity::toStore)

    @SoftDeletedFilter
    override fun findAllByIds(storeIds: List<Long>): List<Store> =
        storeRdbRepository
            .findAllByIdIn(storeIds)
            .map { StoreJpaEntity.toStore(it) }

    @SoftDeletedFilter
    override fun findByOwnerId(storeOwnerId: Long): List<Store> =
        storeRdbRepository
            .findByStoreOwnerIdWithAddress(storeOwnerId)
            .map { StoreJpaEntity.toStore(it) }

    override fun save(store: Store): Store =
        StoreJpaEntity.toStore(
            storeRdbRepository.save(
                StoreJpaEntity.from(store),
            ),
        )

    override fun deleteById(id: Long): Boolean = storeRdbRepository.softDeleteById(id) > 0

    override fun findByUpdatedAt(pivotTime: LocalDateTime): List<Store> =
        storeRdbRepository
            .findByUpdatedAt(pivotTime)
            .map { StoreJpaEntity.toStore(it) }
}
