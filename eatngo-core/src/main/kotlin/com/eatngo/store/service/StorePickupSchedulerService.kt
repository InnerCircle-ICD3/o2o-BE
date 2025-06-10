package com.eatngo.store.service

import com.eatngo.common.exception.store.StoreException
import com.eatngo.extension.executeWithRetry
import com.eatngo.store.event.StorePickupEndedEvent
import com.eatngo.store.infra.StorePersistence
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * 매장 픽업 종료 시간 기반 자동 닫기 서비스
 */
@Service
class StorePickupSchedulerService(
    private val storePersistence: StorePersistence,
    private val eventPublisher: ApplicationEventPublisher,
    @Value("\${store.scheduler.window.start-minutes:31}")
    private val windowStartMinutes: Long = 31,
    @Value("\${store.scheduler.window.end-minutes:1}")
    private val windowEndMinutes: Long = 1,
    @Value("\${store.scheduler.retry.max-attempts:3}")
    private val maxRetryAttempts: Int = 3,
    @Value("\${store.scheduler.retry.delay-ms:5000}")
    private val retryDelayMs: Long = 5000
) {

    /**
     * 픽업 종료 시간이 지난 매장들을 자동으로 닫기
     * 
     * 이중 필터링 구조:
     * 1. SQL 레벨: 대략적인 시간 윈도우로 후보 매장들을 빠르게 조회 (성능 최적화)
     * 2. 도메인 레벨: 정확한 픽업 종료 시간 검증 (정확성 보장)
     * 
     * @return 닫힌 매장 ID 리스트
     */
    @Transactional
    fun closeStoresByPickupEndTime(): List<Long> {
        val now = LocalDateTime.now()
        
        val dayOfWeek = now.dayOfWeek.name
        val endTime = now.toLocalTime().minusMinutes(windowEndMinutes)
        val startTime = now.toLocalTime().minusMinutes(windowStartMinutes)
        
        // 1차 필터링: SQL 레벨에서 시간 윈도우 기반 대략적 후보 조회
        // - 성능: 인덱스 활용으로 빠른 조회
        // - 목적: closeTime이 현재 시점 기준 startTime~endTime 범위에 있는 매장들
        val candidateStores = storePersistence.findOpenStoresForScheduler(
            dayOfWeek, startTime, endTime
        )
        
        // 2차 필터링: 도메인 로직으로 정확한 픽업 종료 시간 재검증
        // - 정확성: 실제 현재 시점이 closeTime을 지났는지 정밀 검증
        // - 목적: false positive 제거 및 비즈니스 로직 적용
        val storesToClose = candidateStores
            .filter { it.isPickupTimeEnded(now.dayOfWeek, now.toLocalTime()) }
            .map { it.id }
        
        if (storesToClose.isNotEmpty()) {
            val updatedCount = executeWithRetry(
                maxAttempts = maxRetryAttempts,
                delayMs = retryDelayMs
            ) { 
                storePersistence.batchUpdateStatusToClosed(storesToClose)
            }
            
            if (updatedCount != storesToClose.size) {
                throw StoreException.StoreBatchUpdateFailed(
                    expectedCount = storesToClose.size,
                    actualCount = updatedCount,
                    storeIds = storesToClose
                )
            }

            eventPublisher.publishEvent(
                StorePickupEndedEvent(
                    closedStoreIds = storesToClose,
                    closedAt = now
                )
            )
        }
        
        return storesToClose
    }
} 