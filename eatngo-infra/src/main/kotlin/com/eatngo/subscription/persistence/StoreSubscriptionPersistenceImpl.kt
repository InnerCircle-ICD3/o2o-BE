package com.eatngo.subscription.persistence

import com.eatngo.aop.SoftDeletedFilter
import com.eatngo.common.response.Cursor
import com.eatngo.subscription.domain.StoreSubscription
import com.eatngo.subscription.dto.CustomerSubscriptionQueryParamDto
import com.eatngo.subscription.dto.StoreOwnerSubscriptionQueryParamDto
import com.eatngo.subscription.dto.StoreSubscriptionQueryParamDto
import com.eatngo.subscription.infra.StoreSubscriptionPersistence
import com.eatngo.subscription.rdb.entity.StoreSubscriptionJpaEntity
import com.eatngo.subscription.rdb.repository.StoreSubscriptionJpaRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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

    override fun findAllByUserIdAndStoreId(userId: Long?, storeId: Long): StoreSubscription? {
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

    override fun findAllByQueryParameter(queryParam: StoreSubscriptionQueryParamDto): Cursor<StoreSubscription> {
        val pageRequest = PageRequest.of(0, 10, Sort.by("id").descending())

        val cursoredSubscriptionJpaEntities = when (queryParam) {
            is CustomerSubscriptionQueryParamDto -> {
                storeSubscriptionRdbRepository.cursoredFindByUserId(
                    userId = queryParam.customerId,
                    lastId = queryParam.lastId,
                    pageable = pageRequest
                )
            }
            is StoreOwnerSubscriptionQueryParamDto -> {
                storeSubscriptionRdbRepository.cursoredFindByStoreId(
                    storeId = queryParam.storeId,
                    lastId = queryParam.lastId,
                    pageable = pageRequest
                )
            }
            else -> throw IllegalArgumentException("지원하지 않는 쿼리 파라미터 타입입니다: ${queryParam::class.simpleName}")
        }

        val nextLastId = if (cursoredSubscriptionJpaEntities.hasNext()) {
            cursoredSubscriptionJpaEntities.content.lastOrNull()?.id
        } else {
            null
        }

        return Cursor.from(
            cursoredSubscriptionJpaEntities.content
                .map(StoreSubscriptionJpaEntity::toSubscription),
            nextLastId
        )
    }
} 