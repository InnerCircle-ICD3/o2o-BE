package com.eatngo.review.rdb.entity

import com.eatngo.common.BaseJpaEntity
import jakarta.persistence.*
import java.math.BigDecimal

/**
 * 매장별 리뷰 집계 테이블
 * 30분마다 배치로 업데이트
 */
@Entity
@Table(
    name = "store_review_stats",
    indexes = [
        Index(name = "idx_store_review_stats_store_id", columnList = "storeId"),
        Index(name = "idx_store_review_stats_updated_at", columnList = "updatedAt")
    ]
)
class StoreReviewStatsJpaEntity(
    @Id
    val storeId: Long,
    
    @Column(nullable = false)
    var totalReviewCount: Int = 0,
    
    @Column(nullable = false, precision = 3, scale = 2)
    var averageRating: BigDecimal = BigDecimal.ZERO,
    
    @Column(nullable = false)
    var scoreSum: Long = 0,
    
    @Column(nullable = false)
    var score1Count: Int = 0,
    
    @Column(nullable = false)
    var score2Count: Int = 0,
    
    @Column(nullable = false)
    var score3Count: Int = 0,
    
    @Column(nullable = false)
    var score4Count: Int = 0,
    
    @Column(nullable = false)
    var score5Count: Int = 0
) : BaseJpaEntity() {
    
    /**
     * 집계 데이터 업데이트
     */
    fun updateStats(
        totalCount: Int,
        scoreSum: Long,
        score1Count: Int,
        score2Count: Int,
        score3Count: Int,
        score4Count: Int,
        score5Count: Int
    ) {
        this.totalReviewCount = totalCount
        this.scoreSum = scoreSum
        this.averageRating = if (totalCount > 0) {
            BigDecimal(scoreSum).divide(BigDecimal(totalCount), 2, BigDecimal.ROUND_HALF_UP)
        } else {
            BigDecimal.ZERO
        }
        this.score1Count = score1Count
        this.score2Count = score2Count
        this.score3Count = score3Count
        this.score4Count = score4Count
        this.score5Count = score5Count
    }
    
    companion object {
        fun create(storeId: Long): StoreReviewStatsJpaEntity {
            return StoreReviewStatsJpaEntity(storeId = storeId)
        }
    }
} 