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
     * @return 닫힌 매장 ID 리스트
     */
    @Transactional
    fun closeStoresByPickupEndTime(): List<Long> {
        val now = LocalDateTime.now()
        
        val dayOfWeek = now.dayOfWeek.name
        val endTime = now.toLocalTime().minusMinutes(windowEndMinutes)
        val startTime = now.toLocalTime().minusMinutes(windowStartMinutes)
        
        val candidateStores = storePersistence.findOpenStoresForScheduler(
            dayOfWeek, startTime, endTime
        )
        
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