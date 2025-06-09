package com.eatngo.review.rdb.repository

import com.eatngo.review.rdb.entity.StoreReviewStatsJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface StoreReviewStatsRdbRepository : JpaRepository<StoreReviewStatsJpaEntity, Long> {
    
    /**
     * 매장 ID들로 집계 데이터 조회
     */
    fun findAllByStoreIdIn(storeIds: List<Long>): List<StoreReviewStatsJpaEntity>
    
    /**
     * 리뷰 집계 데이터 계산 쿼리
     * 주문 테이블을 통해 매장별 리뷰 통계를 계산
     */
    @Query("""
        SELECT 
            o.storeId as storeId,
            COUNT(r.id) as totalCount,
            COALESCE(SUM(r.score), 0) as scoreSum,
            COUNT(CASE WHEN r.score = 1 THEN 1 END) as score1Count,
            COUNT(CASE WHEN r.score = 2 THEN 1 END) as score2Count,
            COUNT(CASE WHEN r.score = 3 THEN 1 END) as score3Count,
            COUNT(CASE WHEN r.score = 4 THEN 1 END) as score4Count,
            COUNT(CASE WHEN r.score = 5 THEN 1 END) as score5Count
        FROM ReviewJpaEntity r
        JOIN OrderJpaEntity o ON r.orderId = o.id
        WHERE r.deletedAt IS NULL 
        AND o.deletedAt IS NULL
        GROUP BY o.storeId
    """, nativeQuery = false)
    fun calculateReviewStatsByStore(): List<ReviewStatsProjection>
    
    /**
     * 특정 매장의 리뷰 집계 데이터 계산
     */
    @Query("""
        SELECT 
            o.storeId as storeId,
            COUNT(r.id) as totalCount,
            COALESCE(SUM(r.score), 0) as scoreSum,
            COUNT(CASE WHEN r.score = 1 THEN 1 END) as score1Count,
            COUNT(CASE WHEN r.score = 2 THEN 1 END) as score2Count,
            COUNT(CASE WHEN r.score = 3 THEN 1 END) as score3Count,
            COUNT(CASE WHEN r.score = 4 THEN 1 END) as score4Count,
            COUNT(CASE WHEN r.score = 5 THEN 1 END) as score5Count
        FROM ReviewJpaEntity r
        JOIN OrderJpaEntity o ON r.orderId = o.id
        WHERE r.deletedAt IS NULL 
        AND o.deletedAt IS NULL
        AND o.storeId = :storeId
        GROUP BY o.storeId
    """, nativeQuery = false)
    fun calculateReviewStatsByStoreId(storeId: Long): ReviewStatsProjection?
}

/**
 * 리뷰 집계 데이터 프로젝션
 */
interface ReviewStatsProjection {
    val storeId: Long
    val totalCount: Long
    val scoreSum: Long
    val score1Count: Long
    val score2Count: Long
    val score3Count: Long
    val score4Count: Long
    val score5Count: Long
} 