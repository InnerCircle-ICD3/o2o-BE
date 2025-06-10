package com.eatngo.store.service

import com.eatngo.store.event.StorePickupEndedEvent
import com.eatngo.store.infra.StorePersistence
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
    private val eventPublisher: ApplicationEventPublisher
) {

    /**
     * 픽업 종료 시간이 지난 매장들을 자동으로 닫기
     * @return 닫힌 매장 ID 리스트
     */
    @Transactional
    fun closeStoresByPickupEndTime(): List<Long> {
        val now = LocalDateTime.now()
        
        val dayOfWeek = now.dayOfWeek.toString()
        val endTime = now.toLocalTime().minusMinutes(1)
        val startTime = now.toLocalTime().minusMinutes(31)
        
        val candidateStores = storePersistence.findOpenStoresForScheduler(
            dayOfWeek, startTime, endTime
        )
        
        val storesToClose = candidateStores
            .filter { it.isPickupTimeEnded(now.dayOfWeek, now.toLocalTime()) }
            .map { it.id }
        
        if (storesToClose.isNotEmpty()) {
            storePersistence.batchUpdateStatusToClosed(storesToClose)

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