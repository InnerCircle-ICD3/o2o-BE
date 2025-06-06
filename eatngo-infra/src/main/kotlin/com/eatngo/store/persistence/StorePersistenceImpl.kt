package com.eatngo.store.persistence

import com.eatngo.aop.SoftDeletedFilter
import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.domain.Store
import com.eatngo.store.infra.StorePersistence
import com.eatngo.store.rdb.entity.StoreJpaEntity
import com.eatngo.store.rdb.repository.StoreRdbRepository
import com.eatngo.store.rdb.json_converter.BusinessHourJson
import com.eatngo.store.rdb.json_converter.StoreCategoryJson
import com.eatngo.store.rdb.json_converter.FoodCategoryJson
import org.springframework.stereotype.Component

/**
 * 매장 영속성 구현체
 */
@Component
class StorePersistenceImpl(
    private val storeRdbRepository: StoreRdbRepository
) : StorePersistence {

    @SoftDeletedFilter
    override fun findById(id: Long): Store? =
        storeRdbRepository
            .findById(id)
            .map(StoreJpaEntity::toStore)
            .orElse(null)

    @SoftDeletedFilter
    override fun findAllByIds(storeIds: List<Long>): List<Store> =
        storeRdbRepository.findAllByIdIn(storeIds)
            .map { StoreJpaEntity.toStore(it) }

    @SoftDeletedFilter
    override fun findByOwnerId(storeOwnerId: Long): List<Store> =
        storeRdbRepository.findByStoreOwnerIdWithAddress(storeOwnerId)
            .map { StoreJpaEntity.toStore(it) }

    override fun save(store: Store): Store =
        StoreJpaEntity.toStore(
            storeRdbRepository.save(
                StoreJpaEntity.from(store)
            )
        )

    override fun updateStatus(storeId: Long, status: StoreEnum.StoreStatus): Boolean =
        storeRdbRepository.updateStatus(storeId, status) > 0

    override fun deleteById(id: Long): Boolean =
        storeRdbRepository.softDeleteById(id) > 0

}