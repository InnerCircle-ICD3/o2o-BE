package com.eatngo.store.scheduler

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.service.StorePickupSchedulerService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * 매장 픽업 종료 시간 확인 스케줄러
 * 30분마다 실행하여 픽업 종료 시간이 지난 매장들을 자동으로 닫음
 * 매시 1분, 31분에 실행 (픽업 시간 종료 1분 후)
 * 픽업 종료 시간이 지난 매장들을 CLOSED 상태로 변경
 */
@Component
class StorePickupEndScheduler(
    private val storePickupSchedulerService: StorePickupSchedulerService
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(cron = "0 1,31 * * * *")
    fun checkAndCloseStoresByPickupEndTime() {
        logger.info("매장 픽업 종료 시간 확인 스케줄러 시작")
        
        try {
            val closedStoreIds = storePickupSchedulerService.closeStoresByPickupEndTime()
            
            if (closedStoreIds.isNotEmpty()) {
                logger.info("픽업 종료로 인해 자동 닫힌 매장 수: ${closedStoreIds.size}, 매장 ID: $closedStoreIds")
            } else {
                logger.debug("픽업 종료로 닫힐 매장이 없습니다.")
            }
        } catch (e: Exception) {
            logger.error("픽업 종료 시간 확인 스케줄러 실행 중 오류 발생", e)
        }
        
        logger.info("매장 픽업 종료 시간 확인 스케줄러 완료")
    }
}

/**
 * 스케줄러용 Store Projection
 */
interface StoreSchedulerProjection {
    val id: Long
    val businessHours: String // JSON 문자열
    val status: StoreEnum.StoreStatus
}