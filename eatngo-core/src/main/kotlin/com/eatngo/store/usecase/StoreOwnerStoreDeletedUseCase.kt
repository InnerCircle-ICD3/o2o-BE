package com.eatngo.store.usecase

import com.eatngo.store.dto.StoreDto
import com.eatngo.store.event.StoreEvent
import com.eatngo.store.service.StoreService
import jakarta.transaction.Transactional
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class StoreOwnerStoreDeletedUseCase(
    private val storeService: StoreService,
    private val eventPublisher: ApplicationEventPublisher
) {
    @Transactional
    fun delete(storeId: Long, storeOwnerId: Long): Long {
        val isDeleted = storeService.deleteStore(storeId, storeOwnerId)

        StoreEvent.fromDelete(isDeleted, storeId, storeOwnerId)
            ?.let { eventPublisher.publishEvent(it) }

        return storeId
    }
}