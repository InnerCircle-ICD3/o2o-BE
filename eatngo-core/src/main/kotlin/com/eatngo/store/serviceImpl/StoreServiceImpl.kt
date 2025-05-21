package com.eatngo.store.serviceImpl

import com.eatngo.common.constant.StoreEnum
import com.eatngo.common.exception.StoreException
import com.eatngo.store.domain.Address
import com.eatngo.store.domain.Store
import com.eatngo.store.dto.PickUpInfoDto
import com.eatngo.store.dto.StoreCreateDto
import com.eatngo.store.dto.StoreDto
import com.eatngo.store.dto.StoreUpdateDto
import com.eatngo.store.dto.extension.toDomain
import com.eatngo.store.dto.extension.toDto
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
        val store = Store.create(request)
        val savedStore = storePersistence.save(store)
        return Store.from(savedStore).toDto()
    }

    override fun updateStore(id: Long, request: StoreUpdateDto): StoreDto {
        val existingStore = storePersistence.findById(id) ?: throw StoreException.StoreNotFound(id)

        val updatedStore = existingStore.update(
            name = request.name,
            description = request.description,
            address = request.address?.toDomain(),
            contactNumber = request.contactNumber,
            imageUrl = request.mainImageUrl,
            businessHours = request.businessHours?.toDomain(),
            storeCategoryInfo = request.storeCategoryInfo?.toDomain()!!,
            pickUpInfo = request.pickUpInfo?.toDomain()!!,
        )

        val savedStore = storePersistence.save(updatedStore)
        return savedStore.toDto()
    }

    override fun updateStoreStatus(id: Long, hasStock: Boolean, now: LocalDateTime): StoreDto {
        val existingStore = storePersistence.findById(id) ?: throw StoreException.StoreNotFound(id)
        existingStore.updateStoreStatus(now, hasStock)
        return storePersistence.save(existingStore).toDto()
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
        val savedStore = storePersistence.save(existingStore)
        return savedStore.toDto()
    }

    override fun updateStorePickupInfo(id: Long, request: PickUpInfoDto): StoreDto {
        val existingStore = storePersistence.findById(id) ?: throw StoreException.StoreNotFound(id)

        existingStore.updatePickupInfo(request.toDomain())

        return storePersistence.save(existingStore).toDto()
    }

    override fun getStoreDetail(id: Long): StoreDto {
        val store = storePersistence.findById(id) ?: throw StoreException.StoreNotFound(id)
        return store.toDto()
    }

    override fun getStoreByOwnerId(storeOwnerId: Long): List<StoreDto> {
        val store = storePersistence.findByOwnerId(storeOwnerId)
        return store.map { it.toDto() }
    }

    override fun deleteStore(id: Long): Boolean {
        val store = storePersistence.findById(id) ?: throw StoreException.StoreNotFound(id)
        store.softDelete()
        storePersistence.save(store)
        return true
    }
}