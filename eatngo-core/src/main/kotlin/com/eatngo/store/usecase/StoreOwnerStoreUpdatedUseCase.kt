package com.eatngo.store.usecase

import com.eatngo.store.dto.StoreDto
import com.eatngo.store.dto.StoreUpdateDto
import com.eatngo.store.event.StoreEvent
import com.eatngo.store.service.StoreService
import jakarta.transaction.Transactional
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class StoreOwnerStoreUpdatedUseCase(
    private val storeService: StoreService,
    private val eventPublisher: ApplicationEventPublisher
) {
    @Transactional
    fun update(storeId: Long, storeOwnerId: Long, request: StoreUpdateDto): StoreDto {
        val store = storeService.getStoreById(storeId)
        val previousStatus = store.status

        val savedStore = storeService.updateStore(storeId, request)

        eventPublisher.publishEvent(StoreEvent.fromInfoUpdate(savedStore, storeOwnerId))

        // 상태가 변경됐다면 상태 변경 이벤트도 발행
        StoreEvent.fromStatusChange(savedStore, storeOwnerId, previousStatus)
            ?.let { eventPublisher.publishEvent(it) }

        return StoreDto.from(savedStore)
    }
}