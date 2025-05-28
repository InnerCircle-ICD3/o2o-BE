package com.eatngo.store_owner.persistence

import com.eatngo.aop.SoftDeletedFilter
import com.eatngo.store_owner.domain.StoreOwner
import com.eatngo.store_owner.infra.StoreOwnerPersistence
import com.eatngo.store_owner.rdb.entity.StoreOwnerJpaEntity
import com.eatngo.store_owner.rdb.repository.StoreOwnerRdbRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class StoreOwnerPersistenceImpl(
    private val storeOwnerRdbRepository: StoreOwnerRdbRepository,
) : StoreOwnerPersistence {
    override fun save(storeOwner: StoreOwner) =
        storeOwnerRdbRepository.save(StoreOwnerJpaEntity.from(storeOwner))
            .let { StoreOwnerJpaEntity.toStoreOwner(it) }

    @SoftDeletedFilter
    override fun findById(id: Long) =
        storeOwnerRdbRepository.findById(id)
            .orElse(null)
            ?.let { StoreOwnerJpaEntity.toStoreOwner(it) }

    @SoftDeletedFilter
    override fun deleteById(id: Long) {
        storeOwnerRdbRepository.softDeleteById(id)
    }

    @SoftDeletedFilter
    override fun findByUserId(userId: Long) =
        storeOwnerRdbRepository.findByAccount_Id(userId)
            ?.let { StoreOwnerJpaEntity.toStoreOwner(it) }
}