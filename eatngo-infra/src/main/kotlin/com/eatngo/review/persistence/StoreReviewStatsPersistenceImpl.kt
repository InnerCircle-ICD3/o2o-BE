package com.eatngo.review.persistence

import com.eatngo.review.dto.StoreReviewStatsDto
import com.eatngo.review.infra.StoreReviewStatsPersistence
import com.eatngo.review.rdb.entity.StoreReviewStatsJpaEntity
import com.eatngo.review.rdb.repository.StoreReviewStatsRdbRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * 매장별 리뷰 집계 영속성 구현체
 */
@Component
class StoreReviewStatsPersistenceImpl(
    private val storeReviewStatsRdbRepository: StoreReviewStatsRdbRepository
) : StoreReviewStatsPersistence {

    override fun findByStoreId(storeId: Long): StoreReviewStatsDto? {
        return storeReviewStatsRdbRepository.findById(storeId)
            .map { toDto(it) }
            .orElse(null)
    }

    override fun findAllByStoreIds(storeIds: List<Long>): List<StoreReviewStatsDto> {
        return storeReviewStatsRdbRepository.findAllByStoreIdIn(storeIds)
            .map { toDto(it) }
    }

    @Transactional
    override fun updateAllStoreReviewStats() {
        val reviewStats = storeReviewStatsRdbRepository.calculateReviewStatsByStore()
        val storeIds = reviewStats.map { it.storeId }
        val existingEntities = storeReviewStatsRdbRepository.findAllByStoreIdIn(storeIds)
            .associateBy { it.storeId }

        val entitiesToSave = reviewStats.map { stats ->
            val entity = existingEntities[stats.storeId]
                ?: StoreReviewStatsJpaEntity.create(stats.storeId)

            entity.updateStats(
                totalCount = stats.totalCount.toInt(),
                scoreSum = stats.scoreSum,
                score1Count = stats.score1Count.toInt(),
                score2Count = stats.score2Count.toInt(),
                score3Count = stats.score3Count.toInt(),
                score4Count = stats.score4Count.toInt(),
                score5Count = stats.score5Count.toInt()
            )
            entity
        }

        storeReviewStatsRdbRepository.saveAll(entitiesToSave)
    }

    private fun toDto(entity: StoreReviewStatsJpaEntity): StoreReviewStatsDto {
        return StoreReviewStatsDto(
            storeId = entity.storeId,
            totalReviewCount = entity.totalReviewCount,
            averageRating = entity.averageRating,
            scoreSum = entity.scoreSum,
            score1Count = entity.score1Count,
            score2Count = entity.score2Count,
            score3Count = entity.score3Count,
            score4Count = entity.score4Count,
            score5Count = entity.score5Count
        )
    }
}