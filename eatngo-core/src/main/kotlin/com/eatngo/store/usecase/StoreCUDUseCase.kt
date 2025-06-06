package com.eatngo.store.usecase

import com.eatngo.store.dto.StoreCreateDto
import com.eatngo.store.dto.StoreDto
import com.eatngo.store.dto.StoreUpdateDto
import com.eatngo.store.event.StoreCUDEvent
import com.eatngo.store.event.StoreCUDEventType
import com.eatngo.store.service.StoreService
import jakarta.transaction.Transactional
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
@Transactional
class StoreCUDUseCase(
    private val storeService: StoreService,
    private val eventPublisher: ApplicationEventPublisher
) {
    
    fun createStore(request: StoreCreateDto): StoreDto {
        val store = storeService.createStore(request)
        
        eventPublisher.publishEvent(
            StoreCUDEvent(
                storeId = store.id,
                userId = request.storeOwnerId,
                eventType = StoreCUDEventType.CREATED
            )
        )
        
        return StoreDto.from(store)
    }
    
    fun updateStore(storeId: Long, storeOwnerId: Long, request: StoreUpdateDto): StoreDto {
        val updatedStore = storeService.updateStore(storeId, request)
        
        eventPublisher.publishEvent(
            StoreCUDEvent(
                storeId = updatedStore.id,
                userId = storeOwnerId,
                eventType = StoreCUDEventType.UPDATED
            )
        )
        
        return StoreDto.from(updatedStore)
    }
    
    fun deleteStore(storeId: Long, storeOwnerId: Long): Long {
        val isDeleted = storeService.deleteStore(storeId, storeOwnerId)
        
        if (isDeleted) {
            eventPublisher.publishEvent(
                StoreCUDEvent(
                    storeId = storeId,
                    userId = storeOwnerId,
                    eventType = StoreCUDEventType.DELETED
                )
            )
        }
        
        return storeId
    }
} 