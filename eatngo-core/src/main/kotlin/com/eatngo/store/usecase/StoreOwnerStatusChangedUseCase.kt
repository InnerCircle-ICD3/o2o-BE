package com.eatngo.store.usecase

import com.eatngo.common.constant.StoreEnum
import com.eatngo.common.exception.store.StoreException
import com.eatngo.store.dto.StoreDto
import com.eatngo.store.event.StoreStatusChangedEvent
import com.eatngo.store.infra.StoreTotalStockRedisRepository
import com.eatngo.store.service.StoreService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class StoreOwnerStatusChangeUseCase(
    private val storeService: StoreService,
    private val eventPublisher: ApplicationEventPublisher,
    private val storeTotalStockRedisRepository: StoreTotalStockRedisRepository
) {
    @Transactional
    fun change(storeId: Long, storeOwnerId: Long, status: StoreEnum.StoreStatus): StoreDto {
        val store = storeService.getStoreById(storeId)
        val previousStatus = store.status
        //권한확인
        store.requireOwner(storeOwnerId)
        //오픈 변경 전 재고부터 확인
        if (status == StoreEnum.StoreStatus.OPEN) validateStockForOpen(storeId)

        val updatedStore = storeService.updateStoreStatus(storeId, status, storeOwnerId)

        if (updatedStore.status != previousStatus) {
            // 매장 상태 변경 이벤트 발행
            eventPublisher.publishEvent(
                StoreStatusChangedEvent(
                    storeId = updatedStore.id,
                    userId = storeOwnerId,
                    previousStatus = previousStatus,
                    currentStatus = updatedStore.status
                )
            )
        }

        return StoreDto.from(updatedStore)
    }
    
    /**
     * 매장 오픈을 위한 재고 검증
     * Redis에서 오늘 날짜의 총 재고를 조회하여 확인
     */
    private fun validateStockForOpen(storeId: Long) {
        val today = LocalDate.now()
        val totalStock = storeTotalStockRedisRepository.getStoreTotalStock(storeId, today)
        
        // totalStock이 null(-1)이거나 0인 경우 오픈 불가
        if (totalStock == null || totalStock <= 0) {
            throw StoreException.StoreCannotOpenNoStock(storeId, totalStock ?: -1)
        }
    }
}