package com.eatngo.store.usecase

import com.eatngo.store.dto.StoreDto
import com.eatngo.store.event.publisher.StoreEventPublisher
import com.eatngo.store.service.StoreService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 재고 상태에 따라 매장 상태를 자동으로 변경하는 UseCase
 */
@Service
class StoreStatusAutoChangeUseCase(
    private val storeService: StoreService,
    private val storeEventPublisher: StoreEventPublisher
) {
    /**
     * 재고 상태에 따라 매장 상태를 자동으로 변경하고 관련 이벤트를 발행합니다.
     * 
     * @param storeId 매장 ID
     * @param hasStock 재고 보유 여부
     * @return 변경된 매장 정보
     */
    @Transactional
    fun changeStatusByInventory(storeId: Long, hasStock: Boolean): StoreDto {
        val store = storeService.getStoreById(storeId)
        val previousStatus = store.status
        
        log.info("매장 상태 자동 변경 시작: storeId={}, hasStock={}, previousStatus={}", storeId, hasStock, previousStatus)
        
        // 매장 상태 변경
        val updatedStore = storeService.updateStoreStatus(storeId, hasStock)
        
        // 재고 상태 변경 이벤트 발행
        storeEventPublisher.publishStoreInventoryChanged(updatedStore, hasStock)
        
        // 상태가 변경되었다면 상태 변경 이벤트 발행
        if (updatedStore.status != previousStatus) {
            storeEventPublisher.publishStoreStatusChanged(updatedStore, SYSTEM_USER_ID, previousStatus)
        }
        
        return StoreDto.from(updatedStore)
    }
    
    companion object {
        // 시스템에 의한 자동 변경임을 표시하기 위한 특별 ID
        private const val SYSTEM_USER_ID = 0L
        private val log = LoggerFactory.getLogger(StoreStatusAutoChangeUseCase::class.java)
    }
} 