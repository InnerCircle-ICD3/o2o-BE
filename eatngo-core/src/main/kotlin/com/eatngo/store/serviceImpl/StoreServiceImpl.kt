package com.eatngo.store.serviceImpl

import com.eatngo.common.constant.StoreEnum
import com.eatngo.common.exception.StoreException
import com.eatngo.store.domain.Store
import com.eatngo.store.dto.PickUpInfoDto
import com.eatngo.store.dto.StoreCreateDto
import com.eatngo.store.dto.StoreDto
import com.eatngo.store.dto.StoreUpdateDto
import com.eatngo.store.infra.StorePersistence
import com.eatngo.store.service.StoreService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * 상점 서비스 구현체
 */
@Service
class StoreServiceImpl(
    private val storePersistence: StorePersistence,
) : StoreService {
    /**
     * 상점 생성
     */
    override fun createStore(request: StoreCreateDto): StoreDto {
        return StoreDto.from(storePersistence.save(Store.create(request)))
    }

    override fun updateStore(id: Long, request: StoreUpdateDto): StoreDto {
        val existingStore = storePersistence.findById(id) ?: throw StoreException.StoreNotFound(id)
        return StoreDto.from(storePersistence.save(existingStore.update(request)))
    }

    override fun updateStoreStatus(id: Long, hasStock: Boolean, now: LocalDateTime): StoreDto {
        val existingStore = storePersistence.findById(id) ?: throw StoreException.StoreNotFound(id)
        existingStore.updateStoreStatus(now, hasStock)
        return StoreDto.from(storePersistence.save(existingStore))
    }

    override fun updateStoreOnlyStatus(id: Long, newStatus: String): StoreDto {
        val existingStore = storePersistence.findById(id)
            ?: throw StoreException.StoreNotFound(id)
        val enumStatus = try {
            StoreEnum.StoreStatus.valueOf(newStatus.uppercase())
        } catch (e: IllegalArgumentException) {
            throw StoreException.StoreStatusInvalid(newStatus).initCause(e)
        }
        existingStore.updateOnlyStoreStatus(enumStatus)
        return StoreDto.from(storePersistence.save(existingStore))
    }

    override fun updateStorePickupInfo(id: Long, request: PickUpInfoDto): StoreDto {
        val existingStore = storePersistence.findById(id) ?: throw StoreException.StoreNotFound(id)
        existingStore.updatePickupInfo(
            request.pickupDay,
            request.pickupStartTime,
            request.pickupEndTime
        )
        return StoreDto.from(storePersistence.save(existingStore))
    }

    override fun getStoreDetail(id: Long): StoreDto {
        val store = storePersistence.findById(id) ?: throw StoreException.StoreNotFound(id)
        return StoreDto.from(store)
    }

    override fun getStoreByOwnerId(storeOwnerId: Long): List<StoreDto> {
        return storePersistence.findByOwnerId(storeOwnerId).map { StoreDto.from(it) }
    }

    override fun deleteStore(id: Long): StoreDto {
        val store = storePersistence.findById(id) ?: throw StoreException.StoreNotFound(id)
        store.softDelete()
        val savedStore = storePersistence.save(store)
        return StoreDto.from(savedStore)
    }
}