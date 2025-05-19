package com.eatngo.store.serviceImpl

import com.eatngo.common.constant.StoreEnum
import com.eatngo.common.exception.StoreException
import com.eatngo.store.domain.Address
import com.eatngo.store.domain.Store
import com.eatngo.store.dto.PickupInfoUpdateRequest
import com.eatngo.store.dto.StatusUpdateRequest
import com.eatngo.store.dto.StoreCreateDto
import com.eatngo.store.dto.StoreDto
import com.eatngo.store.dto.StoreUpdateDto
import com.eatngo.store.dto.extension.toDomain
import com.eatngo.store.dto.extension.toDto
import com.eatngo.store.infra.StorePersistence
import com.eatngo.store.service.StoreService
import org.springframework.stereotype.Service
import java.time.LocalTime

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
            latitude = request.address.latitude,
            longitude = request.address.longitude
        )

        val businessHours = request.businessHours?.map { it.toDomain() } ?: emptyList()

        val store = Store.create(
            storeOwnerId = request.storeOwnerId,
            name = request.name,
            address = address,
            businessNumber = request.businessNumber,
            pickupStartTime = request.pickupStartTime,
            pickupEndTime = request.pickupEndTime,
            description = request.description,
            contactNumber = request.contactNumber,
            imageUrl = request.imageUrl,
            businessHours = businessHours,
            storeCategory = request.storeCategory,
            foodCategory = request.foodCategory,
            pickupAvailableForTomorrow = request.pickupAvailableForTomorrow
        )

        val savedStore = storePersistence.save(store)
        return savedStore.toDto()
    }

    override suspend fun updateStore(id: Long, request: StoreUpdateDto): StoreDto {
        val existingStore = storePersistence.findById(id) ?: throw StoreException.StoreNotFound(id)

        // 1. BusinessHourDto → BusinessHour 변환
        val businessHours = request.businessHours?.map { it.toDomain() }

        // 2. AddressDto → Address 변환 (조건부)
        val address = request.address.toDomain()

        // 3. 도메인 객체 업데이트
        val updatedStore = existingStore.update(
            name = request.name,
            description = request.description,
            address = address,
            businessNumber = request.businessNumber,
            contactNumber = request.contactNumber,
            imageUrl = request.mainImageUrl,
            businessHours = businessHours,
            storeCategory = request.storeCategory,
            foodCategory = request.foodCategory,
            pickupStartTime = request.pickupStartTime,
            pickupEndTime = request.pickupEndTime,
            pickupAvailableForTomorrow = request.pickupAvailableForTomorrow
        )

        // 4. 저장 후 DTO 변환
        val savedStore = storePersistence.save(updatedStore)
        return savedStore.toDto()
    }

    override suspend fun updateStoreStatus(id: Long, status: String): StoreDto {
        val storeStatus = try {
            StoreEnum.StoreStatus.valueOf(status.uppercase())
        } catch (e: IllegalArgumentException) {
            throw StoreException.StoreStatusInvalid(status)
        }
        val existingStore = storePersistence.findById(id) ?: throw StoreException.StoreNotFound(id)
        val updatedStore = existingStore.updateStatus(storeStatus)
        val savedStore = storePersistence.save(updatedStore)
        return savedStore.toDto()
    }

    override suspend fun updateStorePickupInfo(id: Long, request: PickupInfoUpdateRequest): StoreDto {
        val existingStore = storePersistence.findById(id) ?: throw StoreException.StoreNotFound(id)

        // 픽업 시간 파싱 (DTO → 도메인 변환)
        val pickupStartTime = request.pickupStartTime.let { LocalTime.parse(it) }
        val pickupEndTime = request.pickupEndTime.let { LocalTime.parse(it) }

        val updatedStore = existingStore.updatePickupInfo(
            startTime = pickupStartTime,
            endTime = pickupEndTime,
            availableForTomorrow = request.pickupAvailableForTomorrow
        )

        val savedStore = storePersistence.save(updatedStore)
        return savedStore.toDto()
    }

    override suspend fun getStoreDetail(storeId: Long): StoreDto {
        val store = storePersistence.findById(storeId) ?: throw StoreException.StoreNotFound(storeId)
        return store.toDto()
    }

    override suspend fun getStoreByOwnerId(ownerId: String): StoreDto? {
        val store = storePersistence.findByOwnerId(ownerId) ?: throw StoreException.StoreNotFoundByStoreOwner(ownerId)
        return store.toDto()
    }

    override suspend fun deleteStore(id: Long): Boolean {
        val store = storePersistence.findById(id) ?: throw StoreException.StoreNotFound(id)
        val deletedStore = store.softDelete()
        storePersistence.save(deletedStore)
        return true
    }
}