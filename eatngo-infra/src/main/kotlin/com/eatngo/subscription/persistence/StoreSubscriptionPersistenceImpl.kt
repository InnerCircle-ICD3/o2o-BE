package com.eatngo.subscription.persistence

import com.eatngo.subscription.domain.StoreSubscription
import com.eatngo.subscription.infra.StoreSubscriptionPersistence
import com.eatngo.subscription.rdb.entity.StoreSubscriptionJpaEntity
import com.eatngo.subscription.rdb.repository.StoreSubscriptionJpaRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class StoreSubscriptionPersistenceImpl(
    private val storeSubscriptionRepository: StoreSubscriptionJpaRepository
) : StoreSubscriptionPersistence {

    override fun findById(id: Long): StoreSubscription? {
        return storeSubscriptionRepository.findById(id)
            .map { StoreSubscriptionJpaEntity.toSubscription(it) }
            .orElse(null)
    }

    override fun findByUserId(userId: Long): List<StoreSubscription> {
        return storeSubscriptionRepository.findByUserId(userId)
            .map { StoreSubscriptionJpaEntity.toSubscription(it) }
    }

    override fun findByStoreId(storeId: Long): List<StoreSubscription> {
        return storeSubscriptionRepository.findByStoreId(storeId)
            .map { StoreSubscriptionJpaEntity.toSubscription(it) }
    }

    override fun findByUserIdAndStoreId(userId: Long, storeId: Long): StoreSubscription? {
        return storeSubscriptionRepository.findByUserIdAndStoreId(userId, storeId)
            ?.let { StoreSubscriptionJpaEntity.toSubscription(it) }
    }

    override fun save(subscription: StoreSubscription): StoreSubscription {
        val entity = StoreSubscriptionJpaEntity.from(subscription)
        val savedEntity = storeSubscriptionRepository.save(entity)
        return StoreSubscriptionJpaEntity.toSubscription(savedEntity)
    }

    override fun softDelete(id: Long): Boolean {
        val entity = storeSubscriptionRepository.findById(id).orElse(null) ?: return false
        entity.deletedAt = LocalDateTime.now()
        entity.updatedAt = entity.deletedAt!!
        storeSubscriptionRepository.save(entity)
        return true
    }

    override fun existsByUserIdAndStoreId(userId: Long, storeId: Long): Boolean {
        return storeSubscriptionRepository.existsByUserIdAndStoreId(userId, storeId)
    }

    override fun findAllByStoreIds(storeIds: List<Long>): List<StoreSubscription> {
        return storeSubscriptionRepository.findAllByStoreIds(storeIds)
            .map { StoreSubscriptionJpaEntity.toSubscription(it) }
    }

    override fun findStoreIdsByUserId(userId: Long): List<Long> {
        return storeSubscriptionRepository.findStoreIdsByUserId(userId)
    }
} 