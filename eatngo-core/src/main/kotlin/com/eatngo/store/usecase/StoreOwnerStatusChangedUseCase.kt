package com.eatngo.store.usecase

import com.eatngo.store.dto.StoreDto
import com.eatngo.store.event.StoreEvent
import com.eatngo.store.serviceImpl.StoreServiceImpl
import jakarta.transaction.Transactional
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class StoreOwnerStatusChangeUseCase(
    private val storeService: StoreServiceImpl,
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