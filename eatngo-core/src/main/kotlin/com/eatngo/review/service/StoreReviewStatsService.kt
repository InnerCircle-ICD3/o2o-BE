package com.eatngo.review.service

import com.eatngo.review.dto.StoreReviewStatsDto
import com.eatngo.review.infra.StoreReviewStatsPersistence
import org.springframework.stereotype.Service

/**
 * 매장별 리뷰 집계 서비스
 */
@Service
class StoreReviewStatsService(
    private val storeReviewStatsPersistence: StoreReviewStatsPersistence
) {
    
    /**
     * 매장별 리뷰 집계 데이터 조회 (배치)
     */
    fun getStoreReviewStats(storeIds: List<Long>): Map<Long, StoreReviewStatsDto> {
        return storeReviewStatsPersistence.findAllByStoreIds(storeIds)
            .associateBy { it.storeId }
    }
    
    /**
     * 단일 매장 리뷰 집계 데이터 조회
     */
    fun getStoreReviewStats(storeId: Long): StoreReviewStatsDto? {
        return storeReviewStatsPersistence.findByStoreId(storeId)
    }
    
    /**
     * 모든 매장의 리뷰 집계 데이터 업데이트
     */
    fun updateAllStoreReviewStats() {
        storeReviewStatsPersistence.updateAllStoreReviewStats()
    }
} 