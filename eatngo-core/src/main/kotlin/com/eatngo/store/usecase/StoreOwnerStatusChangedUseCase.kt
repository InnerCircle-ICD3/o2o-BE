package com.eatngo.store.usecase

import com.eatngo.store.dto.StoreDto
import com.eatngo.store.event.StoreEvent
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
    fun change(storeId: Long, storeOwnerId: Long, statusName: String): StoreDto {
        val store = storeService.getStoreById(storeId)
        val previousStatus = store.status
        val updatedStore = storeService.updateStoreStatus(storeId, statusName, storeOwnerId)

        StoreEvent.fromStatusChange(updatedStore, storeOwnerId, previousStatus)
            ?.let { eventPublisher.publishEvent(it) }

        return StoreDto.from(updatedStore)
    }
}