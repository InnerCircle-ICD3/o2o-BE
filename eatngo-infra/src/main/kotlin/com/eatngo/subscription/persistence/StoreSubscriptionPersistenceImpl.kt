package com.eatngo.subscription.persistence

import com.eatngo.aop.SoftDeletedFilter
import com.eatngo.subscription.domain.StoreSubscription
import com.eatngo.subscription.infra.StoreSubscriptionPersistence
import com.eatngo.subscription.rdb.entity.StoreSubscriptionJpaEntity
import com.eatngo.subscription.rdb.repository.StoreSubscriptionJpaRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class StoreSubscriptionPersistenceImpl(
    private val storeSubscriptionRdbRepository: StoreSubscriptionJpaRepository
) : StoreSubscriptionPersistence {

    override fun findById(id: Long): StoreSubscription? {
        return storeSubscriptionRdbRepository.findById(id)
            .map { StoreSubscriptionJpaEntity.toSubscription(it) }
            .orElse(null)
    }

    override fun findByUserId(userId: Long): List<StoreSubscription> {
        return storeSubscriptionRdbRepository.findByUserId(userId)
            .map { StoreSubscriptionJpaEntity.toSubscription(it) }
    }

    override fun findByStoreId(storeId: Long): List<StoreSubscription> {
        return storeSubscriptionRdbRepository.findByStoreId(storeId)
            .map { StoreSubscriptionJpaEntity.toSubscription(it) }
    }

    override fun findAllByUserIdAndStoreId(userId: Long, storeId: Long): StoreSubscription? {
        return storeSubscriptionRdbRepository.findByUserIdAndStoreId(userId, storeId)
            ?.let { StoreSubscriptionJpaEntity.toSubscription(it) }
    }

    override fun save(subscription: StoreSubscription): StoreSubscription {
        val entity = StoreSubscriptionJpaEntity.from(subscription)
        val savedEntity = storeSubscriptionRdbRepository.save(entity)
        return StoreSubscriptionJpaEntity.toSubscription(savedEntity)
    }

    override fun findAllByStoreIds(storeIds: List<Long>): List<StoreSubscription> {
        return storeSubscriptionRdbRepository.findAllByStoreIds(storeIds)
            .map { StoreSubscriptionJpaEntity.toSubscription(it) }
    }

    override fun findStoreIdsByUserId(userId: Long): List<Long> {
        return storeSubscriptionRdbRepository.findStoreIdsByUserId(userId)
    }
} 