package com.eatngo.review.dto

import java.math.BigDecimal

/**
 * 매장별 리뷰 집계 데이터 DTO
 */
data class StoreReviewStatsDto(
    val storeId: Long,
    val totalReviewCount: Int,
    val averageRating: BigDecimal,
    val scoreSum: Long,
    val score1Count: Int,
    val score2Count: Int,
    val score3Count: Int,
    val score4Count: Int,
    val score5Count: Int
) {
    companion object {
        fun getDefault(storeId: Long): StoreReviewStatsDto {
            return StoreReviewStatsDto(
                storeId = storeId,
                totalReviewCount = 0,
                averageRating = BigDecimal.ZERO,
                scoreSum = 0,
                score1Count = 0,
                score2Count = 0,
                score3Count = 0,
                score4Count = 0,
                score5Count = 0
            )
        }
    }
} 