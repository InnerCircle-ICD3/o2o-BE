package com.eatngo.store.usecase

import com.eatngo.store.dto.StoreDto
import com.eatngo.store.event.StoreEvent
import com.eatngo.store.service.StoreService
import jakarta.transaction.Transactional
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class StoreOwnerStatusChangeUseCase(
    private val storeService: StoreService,
    private val eventPublisher: ApplicationEventPublisher
) {
    @Transactional
    fun change(storeId: Long, storeOwnerId: Long, statusName: String): StoreDto {
        val updatedStore = storeService.updateStoreStatus(storeId, statusName, storeOwnerId)

        StoreEvent.from(updatedStore, storeOwnerId)
            ?.let { eventPublisher.publishEvent(it) }

        return StoreDto.from(updatedStore)
    }
}