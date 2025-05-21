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
    override suspend fun createStore(request: StoreCreateDto): StoreDto {
        val address = Address(
            roadAddress = request.address.roadAddress.toDomain(),
            legalAddress = request.address.legalAddress?.toDomain(),
            adminAddress = request.address.adminAddress?.toDomain(),
            coordinate = request.address.coordinate.toDomain(),
        )

        val businessHours = request.businessHours?.map { it.toDomain() } ?: emptyList()

        val store = Store.create(
            storeOwnerId = request.storeOwnerId,
            name = request.name,
            address = address,
            businessNumber = request.businessNumber,
            description = request.description,
            contactNumber = request.contactNumber,
            imageUrl = request.imageUrl,
            businessHours = businessHours,
            storeCategoryInfo = request.storeCategoryInfo.toDomain(),
            pickUpInfo = request.pickUpInfo.toDomain()
        )

        val savedStore = storePersistence.save(store)
        return savedStore.toDto()
    }

    override suspend fun updateStore(id: Long, request: StoreUpdateDto): StoreDto {
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

    override suspend fun updateStoreStatus(id: Long, hasStock: Boolean, now: LocalDateTime): StoreDto {
        val existingStore = storePersistence.findById(id) ?: throw StoreException.StoreNotFound(id)
        existingStore.updateStoreStatus(now, hasStock)
        return storePersistence.save(existingStore).toDto()
    }

    override suspend fun updateStoreOnlyStatus(id: Long, newStatus: String): StoreDto {
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

    override suspend fun updateStorePickupInfo(id: Long, request: PickUpInfoDto): StoreDto {
        val existingStore = storePersistence.findById(id) ?: throw StoreException.StoreNotFound(id)

        existingStore.updatePickupInfo(request.toDomain())

        return storePersistence.save(existingStore).toDto()
    }

    override suspend fun getStoreDetail(id: Long): StoreDto {
        val store = storePersistence.findById(id) ?: throw StoreException.StoreNotFound(id)
        return store.toDto()
    }

    override suspend fun getStoreByOwnerId(storeOwnerId: Long): List<StoreDto> {
        val store = storePersistence.findByOwnerId(storeOwnerId)
        return store.map { it.toDto() }
    }

    override suspend fun deleteStore(id: Long): Boolean {
        val store = storePersistence.findById(id) ?: throw StoreException.StoreNotFound(id)
        store.softDelete()
        storePersistence.save(store)
        return true
    }
}