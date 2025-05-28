package com.eatngo.store.serviceImpl

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.domain.Store
import com.eatngo.store.dto.PickUpInfoDto
import com.eatngo.store.dto.StoreCreateDto
import com.eatngo.store.dto.StoreDto
import com.eatngo.store.dto.StoreUpdateDto
import com.eatngo.store.infra.StorePersistence
import com.eatngo.store.infra.findByIdOrThrow
import com.eatngo.store.infra.requireOwner
import com.eatngo.store.service.StoreService
import org.springframework.stereotype.Service

/**
 * 상점 서비스 구현체
 */
@Service
class StoreServiceImpl(
    private val storePersistence: StorePersistence,
) : StoreService {
    override fun createStore(request: StoreCreateDto): StoreDto {
        val store = Store.create(request)
        val savedStore = storePersistence.save(store)
        return StoreDto.from(savedStore)
    }

    override fun updateStore(id: Long, request: StoreUpdateDto): StoreDto {
        val existingStore = storePersistence.findByIdOrThrow(id)
        existingStore.update(request)
        val savedStore = storePersistence.save(existingStore)
        return StoreDto.from(savedStore)
    }

    override fun updateStoreStatus(id: Long, hasStock: Boolean): StoreDto {
        val existingStore = storePersistence.findByIdOrThrow(id)
        existingStore.updateStoreStatus(hasStock)
        val savedStore = storePersistence.save(existingStore)
        return StoreDto.from(savedStore)
    }

    override fun updateStoreOnlyStatus(id: Long, newStatus: String, storeOwnerId: Long): StoreDto {
        val existingStore = storePersistence.findByIdOrThrow(id)
        existingStore.requireOwner(storeOwnerId)

        val status = StoreEnum.StoreStatus.valueOf(newStatus.uppercase())

        existingStore.updateOnlyStoreStatus(status)
        val savedStore = storePersistence.save(existingStore)
        return StoreDto.from(savedStore)
    }

    override fun updateStorePickupInfo(id: Long, request: PickUpInfoDto, storeOwnerId: Long): StoreDto {
        val existingStore = storePersistence.findByIdOrThrow(id)
        existingStore.requireOwner(storeOwnerId)

        existingStore.updatePickupInfo(
            request.pickupDay,
            request.pickupStartTime,
            request.pickupEndTime
        )
        val savedStore = storePersistence.save(existingStore)
        return StoreDto.from(savedStore)
    }

    override fun getStoreDetail(id: Long): StoreDto {
        val store = storePersistence.findByIdOrThrow(id)
        return StoreDto.from(store)
    }

    override fun getStoreDetail(id: Long, storeOwnerId: Long): StoreDto {
        val existingStore = storePersistence.findByIdOrThrow(id)
        existingStore.requireOwner(storeOwnerId)
        return StoreDto.from(existingStore)
    }

    override fun getStoreByOwnerId(storeOwnerId: Long): List<StoreDto> {
        return storePersistence.findByOwnerId(storeOwnerId).map { StoreDto.from(it) }
    }

    override fun deleteStore(id: Long, storeOwnerId: Long): StoreDto {
        val existingStore = storePersistence.findByIdOrThrow(id)
        existingStore.requireOwner(storeOwnerId)

        existingStore.softDelete()
        val savedStore = storePersistence.save(existingStore)
        return StoreDto.from(savedStore)
    }
}