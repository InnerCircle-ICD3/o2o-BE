package com.eatngo.review.infra

import com.eatngo.review.dto.StoreReviewStatsDto

/**
 * 매장별 리뷰 집계 영속성 인터페이스
 */
interface StoreReviewStatsPersistence {
    
    /**
     * 매장 ID로 집계 데이터 조회
     */
    fun findByStoreId(storeId: Long): StoreReviewStatsDto?
    
    /**
     * 매장 ID들로 집계 데이터 배치 조회
     */
    fun findAllByStoreIds(storeIds: List<Long>): List<StoreReviewStatsDto>
    
    /**
     * 모든 매장의 리뷰 집계 데이터 업데이트
     */
    fun updateAllStoreReviewStats()
} 