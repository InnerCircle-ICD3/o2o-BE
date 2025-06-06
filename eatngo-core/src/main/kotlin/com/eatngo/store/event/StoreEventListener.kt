package com.eatngo.store.event

import com.eatngo.store.service.StoreService
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * 매장 이벤트를 처리하는 리스너
 * - 이벤트 발생 시 로깅 및 필요한 후속 처리를 담당
 */
@Component
@Transactional
class StoreEventListener(
    private val storeService: StoreService
) {
    
    @EventListener
    fun handleStoreCUDEvent(event: StoreCUDEvent) {
        when (event.eventType) {
            StoreCUDEventType.CREATED -> {
                log.info("매장 생성 이벤트 처리: storeId={}, userId={}", event.storeId, event.userId)
                // 매장 생성 후속 처리 (알림, 캐시 갱신 등)
                // 필요한 경우: val store = storeService.getStoreById(event.storeId)
            }
            StoreCUDEventType.UPDATED -> {
                log.info("매장 정보 업데이트 이벤트 처리: storeId={}, userId={}", event.storeId, event.userId)
                // 매장 정보 업데이트 후속 처리 (캐시 갱신 등)
                // 필요한 경우: val store = storeService.getStoreById(event.storeId)
            }
            StoreCUDEventType.DELETED -> {
                log.info("매장 삭제 이벤트 처리: storeId={}, userId={}", event.storeId, event.userId)
                // 매장 삭제 후속 처리 (연관 데이터 정리 등)
                // 삭제된 매장은 조회 불가능하므로 ID만 사용
            }
        }
    }
    
    @EventListener
    fun handleStoreStatusChangedEvent(event: StoreStatusChangedEvent) {
        log.info("매장 상태 변경 이벤트 처리: storeId={}, userId={}, 이전상태={}, 현재상태={}", 
            event.storeId, event.userId, event.previousStatus, event.currentStatus)
        // 매장 상태 변경 후속 처리 (알림, 캐시 갱신 등)
        // 필요한 경우: val store = storeService.getStoreById(event.storeId)
    }
    
    @EventListener
    fun handleStoreInventoryChangedEvent(event: StoreInventoryChangedEvent) {
        log.info("매장 재고 상태 변경 이벤트 처리: storeId={}, hasStock={}", 
            event.storeId, event.hasStock)
        // 매장 재고 상태 변경 후속 처리 (알림, 캐시 갱신 등)
        
        val statusMsg = if (event.hasStock) "재고 복구" else "재고 소진"
        log.info("매장({})의 재고 상태가 {}되었습니다.", event.storeId, statusMsg)
        // 필요한 경우: val store = storeService.getStoreById(event.storeId)
    }
    
    companion object {
        @JvmStatic
        val log = LoggerFactory.getLogger(StoreEventListener::class.java)
    }
} 