package com.eatngo.store.usecase

import com.eatngo.store.dto.StoreCreateDto
import com.eatngo.store.dto.StoreDto
import com.eatngo.store.event.StoreEvent
import com.eatngo.store.service.StoreService
import jakarta.transaction.Transactional
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class StoreOwnerStoreCreatedUseCase(
    private val storeService: StoreService,
    private val eventPublisher: ApplicationEventPublisher
) {
    @Transactional
    fun create(request: StoreCreateDto): StoreDto {
        val store = storeService.createStore(request)

        // 이벤트 발행
        StoreEvent.from(store, request.storeOwnerId)
            ?.let { eventPublisher.publishEvent(it) }

        return StoreDto.from(store)
    }
}