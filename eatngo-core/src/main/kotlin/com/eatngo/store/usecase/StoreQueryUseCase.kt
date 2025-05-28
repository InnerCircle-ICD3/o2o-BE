package com.eatngo.store.usecase

import com.eatngo.store.dto.StoreDto
import com.eatngo.store.service.StoreService
import org.springframework.stereotype.Service

@Service
class StoreQueryUseCase (
    private val storeService: StoreService
) {
    fun getStoreById(storeId: Long): StoreDto {
        val store = storeService.getStoreById(storeId)
        return StoreDto.from(store)
    }

    fun getStoresByStoreOwnerId(storeOwnerId: Long): List<StoreDto> {
        val stores = storeService.getStoresByStoreOwnerId(storeOwnerId)
        return stores.map { StoreDto.from(it) }
    }
}