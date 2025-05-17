package com.eatngo.store.serviceImpl

import com.eatngo.common.exception.StoreException
import com.eatngo.store.constant.StoreEnum
import com.eatngo.store.domain.Address
import com.eatngo.store.domain.AdminAddress
import com.eatngo.store.domain.BusinessHour
import com.eatngo.store.domain.LegalAddress
import com.eatngo.store.domain.RoadAddress
import com.eatngo.store.domain.Store
import com.eatngo.store.dto.AdminAddressDto
import com.eatngo.store.dto.CustomerStoreDetailResponse
import com.eatngo.store.dto.CustomerStoreListResponse
import com.eatngo.store.dto.LocationDto
import com.eatngo.store.dto.PickupInfoUpdateRequest
import com.eatngo.store.dto.StatusUpdateRequest
import com.eatngo.store.dto.StoreCreateDto
import com.eatngo.store.dto.StoreDto
import com.eatngo.store.dto.StoreSearchDto
import com.eatngo.store.dto.StoreSummary
import com.eatngo.store.dto.StoreUpdateDto
import com.eatngo.store.infra.StorePersistence
import com.eatngo.store.service.StoreService
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * 상점 서비스 구현체
 */
@Service
class StoreServiceImpl(
    private val storePersistence: StorePersistence
) : StoreService {
    /**
     * 상점 생성
     */
    override suspend fun createStore(request: StoreCreateDto): StoreDto {
        // 필수 필드 검증
        validateCreateRequest(request)

        // 비즈니스 시간 객체 생성
        val businessHours = request.businessHours?.map { hourDto ->
            BusinessHour(
                dayOfWeek = DayOfWeek.valueOf(hourDto.dayOfWeek),
                openTime = LocalTime.parse(hourDto.openTime),
                closeTime = LocalTime.parse(hourDto.closeTime)
            )
        }

        // 주소 생성
        val address = Address(
            roadAddress = request.roadAddress.toDomain(),
            legalAddress = request.legalAddress?.toDomain()!!,
            adminAddress = request.adminAddress?.toDomain(),
            latitude = request.location.lat,
            longitude = request.location.lng
        )

        // 픽업 시간 파싱
        val pickupStartTime = LocalTime.parse(request.pickupStartTime)
        val pickupEndTime = LocalTime.parse(request.pickupEndTime)

        // Store 객체 생성
        val store = Store(
            id = 0L, // 저장 시 자동 생성됨
            storeOwnerId = request.storeOwnerId,
            name = request.name,
            description = request.description,
            address = address,
            businessNumber = request.businessNumber,
            contactNumber = request.contact,
            imageUrl = request.mainImageUrl,
            businessHours = businessHours,
            categories = request.categories.map { it },
            status = StoreEnum.StoreStatus.CLOSED, // 기본값은 CLOSED
            pickupStartTime = pickupStartTime,
            pickupEndTime = pickupEndTime,
            pickupAvailableForTomorrow = request.pickupAvailableForTomorrow,
            createdAt = ZonedDateTime.now(),
            updatedAt = ZonedDateTime.now(),
            deletedAt = null
        )

        val savedStore = storePersistence.save(store)
        return StoreDto.from(savedStore)
    }

    override suspend fun updateStore(id: Long, request: StoreUpdateDto): StoreDto {
        val existingStore = storePersistence.findById(id) ?: throw StoreException.StoreNotFound(id)

        // 비즈니스 시간 객체 생성
        val businessHours = request.businessHours?.map { hourDto ->
            BusinessHour(
                dayOfWeek = DayOfWeek.valueOf(hourDto.dayOfWeek),
                openTime = LocalTime.parse(hourDto.openTime),
                closeTime = LocalTime.parse(hourDto.closeTime)
            )
        }

        // 주소 생성
        val address = if (request.roadAddress != null && request.legalAddress != null && request.location != null) {
            Address(
                roadAddress = request.roadAddress.toDomain(),
                legalAddress = request.legalAddress.toDomain(),
                adminAddress = request.adminAddress?.toDomain(),
                latitude = request.location.lat,
                longitude = request.location.lng
            )
        } else {
            null
        }

        // 픽업 시간 파싱
        val pickupStartTime = request.pickupStartTime?.let { LocalTime.parse(it) }
        val pickupEndTime = request.pickupEndTime?.let { LocalTime.parse(it) }

        // 요청에서 전달된 값으로만 업데이트
        val updatedStore = existingStore.update(
            name = request.name,
            description = request.description,
            address = address,
            businessNumber = request.businessNumber,
            contactNumber = request.contact,
            imageUrl = request.mainImageUrl,
            businessHours = businessHours,
            categories = request.categories?.map { it },
            pickupStartTime = pickupStartTime,
            pickupEndTime = pickupEndTime,
            pickupAvailableForTomorrow = request.pickupAvailableForTomorrow
        )

        val savedStore = storePersistence.save(updatedStore)
        return StoreDto.from(savedStore)
    }

    override suspend fun updateStoreStatus(id: Long, request: StatusUpdateRequest): StoreDto {
        val existingStore = storePersistence.findById(id) ?: throw StoreException.StoreNotFound(id)

        val updatedStore = existingStore.updateStatus(request.status)

        val savedStore = storePersistence.save(updatedStore)
        return StoreDto.from(savedStore)
    }

    override suspend fun updateStorePickupInfo(id: Long, request: PickupInfoUpdateRequest): StoreDto {
        val existingStore = storePersistence.findById(id) ?: throw StoreException.StoreNotFound(id)

        // 픽업 시간 파싱
        val pickupStartTime = request.pickupStartTime?.let { LocalTime.parse(it) }
        val pickupEndTime = request.pickupEndTime?.let { LocalTime.parse(it) }

        val updatedStore = existingStore.updatePickupInfo(
            startTime = pickupStartTime,
            endTime = pickupEndTime,
            availableForTomorrow = request.pickupAvailableForTomorrow
        )

        val savedStore = storePersistence.save(updatedStore)
        return StoreDto.from(savedStore)
    }

    override suspend fun getStoreById(id: Long): StoreDto {
        val store = storePersistence.findById(id) ?: throw StoreException.StoreNotFound(id)
        return StoreDto.from(store)
    }

    /**
     * 스토어 상세 정보 조회 및 CustomerStoreDetailResponse 생성
     */
    override suspend fun getStoreDetailById(storeId: Long): CustomerStoreDetailResponse {
        val store = getStoreById(storeId)
        
        // CustomerStoreDetailResponse 생성
        return CustomerStoreDetailResponse(
            storeId = store.storeId,
            name = store.name,
            roadAddress = store.roadAddress,
            legalAddress = store.legalAddress,
            adminAddress = store.adminAddress,
            location = store.location,
            businessHours = store.businessHours,
            contact = store.contact,
            description = store.description,
            pickupStartTime = store.pickupStartTime,
            pickupEndTime = store.pickupEndTime,
            pickupAvailableForTomorrow = store.pickupAvailableForTomorrow,
            mainImageUrl = store.mainImageUrl,
            status = store.status,
            isAvailableForPickup = store.isAvailableForPickup,
            categories = store.categories,
            isSubscribed = false // 실제 구현시 서비스에서 확인
        )
    }

    /**
     * 스토어 검색 및 CustomerStoreListResponse 생성
     */
    override suspend fun searchStoresWithResponse(request: StoreSearchDto): CustomerStoreListResponse {
        val stores = searchStores(request)
        
        return CustomerStoreListResponse(
            stores = stores,
            totalCount = stores.size,
            offset = request.offset,
            limit = request.limit
        )
    }

    override suspend fun searchStores(request: StoreSearchDto): List<StoreSummary> {
        val stores = when {
            // 위치 기반 검색
            request.location != null -> {
                val address = Address(
                    roadAddress = RoadAddress("", "", null),
                    legalAddress = LegalAddress("", "", null),
                    adminAddress = null,
                    latitude = request.location.lat,
                    longitude = request.location.lng
                )
                storePersistence.findNearby(address, request.radius ?: 5.0, request.limit, request.offset)
            }
            // 카테고리 검색
            request.category != null -> {
                storePersistence.findByCategory(request.category, request.limit, request.offset)
            }
            // 영업 중인 매장만 검색
            request.onlyOpen == true -> {
                storePersistence.findOpenStores(request.limit, request.offset)
            }
            // 기본 키워드 검색
            else -> {
                storePersistence.findByNameContaining(request.keyword ?: "", request.limit, request.offset)
            }
        }

        // 픽업 가능 여부에 따른 필터링
        val filteredStores = if (request.availableForPickup == true) {
            val now = ZonedDateTime.now()
            stores.filter { it.isAvailableForPickup(now) }
        } else {
            stores
        }

        return filteredStores.map { store ->
            request.location?.let { loc ->
                StoreSummary.from(store, loc)
            } ?: StoreSummary.from(store)
        }
    }

    override suspend fun getStoreByOwnerId(ownerId: String): StoreDto? {
        val store = storePersistence.findByOwnerId(ownerId) ?: return null
        return StoreDto.from(store)
    }

    override suspend fun deleteStore(id: Long): Boolean {
        val store = storePersistence.findById(id) ?: throw StoreException.StoreNotFound(id)
        val deletedStore = store.softDelete()
        storePersistence.save(deletedStore)
        return true
    }

    /**
     * 매장 생성 요청 유효성 검증
     */
    private fun validateCreateRequest(request: StoreCreateDto) {
        val missingFields = mutableListOf<String>()

        if (request.name.isBlank()) {
            missingFields.add("매장 이름")
        }

        if (request.roadAddress == null) {
            missingFields.add("도로명 주소")
        }

        if (request.legalAddress == null) {
            missingFields.add("지번 주소")
        }

        if (request.storeOwnerId.isBlank()) {
            missingFields.add("매장 소유자 ID")
        }

        // 추가 검증 로직...

        if (missingFields.isNotEmpty()) {
            val errorMessage = when (missingFields.size) {
                1 -> "${missingFields[0]}은(는) 필수입니다."
                else -> "${missingFields.joinToString(", ")}은(는) 필수입니다."
            }

            // 필드를 키로, 에러 메시지는 빈 문자열로 (필드명만 필요하므로)
            val validationErrors = missingFields.associateWith { "" }

            throw StoreException.StoreValidationErrors(errorMessage, validationErrors)
        }
    }
}