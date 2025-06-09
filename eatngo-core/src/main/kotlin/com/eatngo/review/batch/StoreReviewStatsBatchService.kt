package com.eatngo.review.batch

import com.eatngo.review.service.StoreReviewStatsService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

/**
 * 매장별 리뷰 집계 배치 서비스
 */
@Service
class StoreReviewStatsBatchService(
    private val storeReviewStatsService: StoreReviewStatsService
) {
    private val logger = LoggerFactory.getLogger(StoreReviewStatsBatchService::class.java)

    @Scheduled(cron = "0 */30 * * * *")
    fun updateStoreReviewStats() {
        try {
            logger.info("매장별 리뷰 집계 배치 시작")
            val startTime = System.currentTimeMillis()
            
            storeReviewStatsService.updateAllStoreReviewStats()
            
            val endTime = System.currentTimeMillis()
            logger.info("매장별 리뷰 집계 배치 완료 - 소요시간: {}ms", endTime - startTime)
            
        } catch (e: Exception) {
            logger.error("매장별 리뷰 집계 배치 실행 중 오류 발생", e)
        }
    }
} 