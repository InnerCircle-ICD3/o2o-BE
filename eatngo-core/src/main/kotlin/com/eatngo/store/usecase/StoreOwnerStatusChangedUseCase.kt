package com.eatngo.store.usecase

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.dto.StoreDto
import com.eatngo.store.event.StoreStatusChangedEvent
import com.eatngo.store.service.StoreService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StoreOwnerStatusChangeUseCase(
    private val storeService: StoreService,
    private val eventPublisher: ApplicationEventPublisher
) {
    @Transactional
    fun change(storeId: Long, storeOwnerId: Long, status: StoreEnum.StoreStatus): StoreDto {
        val store = storeService.getStoreById(storeId)
        val previousStatus = store.status
        
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
}