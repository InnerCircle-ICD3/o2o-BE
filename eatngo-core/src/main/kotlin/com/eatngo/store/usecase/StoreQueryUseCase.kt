package com.eatngo.store.usecase

import com.eatngo.store.dto.StoreDto
import com.eatngo.store.service.StoreService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StoreQueryUseCase(
    private val storeService: StoreService
) {
    @Transactional(readOnly = true)
    fun getStoreById(storeId: Long): StoreDto {
        val store = storeService.getStoreById(storeId)
        return StoreDto.from(store)
    }

    @Transactional(readOnly = true)
    fun getStoresByStoreOwnerId(storeOwnerId: Long): List<StoreDto> {
        val stores = storeService.getStoresByStoreOwnerId(storeOwnerId)
        return stores.map { StoreDto.from(it) }
    }
}