package com.eatngo.store.service

import com.eatngo.common.exception.StoreException
import com.eatngo.store.constant.StoreEnum
import com.eatngo.store.domain.Address
import com.eatngo.store.domain.RoadAddress
import com.eatngo.store.domain.LotAddress
import com.eatngo.store.domain.Store
import com.eatngo.store.domain.BusinessHour
import com.eatngo.store.infra.StorePersistence
import com.eatngo.store.dto.*
import org.springframework.stereotype.Service
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.DayOfWeek

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
    override suspend fun createStore(request: StoreDto.CreateRequest): StoreDto.CreateResponse {
        // 필수 필드 검증
        validateCreateRequest(request)
        
        // 비즈니스 시간 객체 생성
        val businessHours = request.businessHours.map { hourDto ->
            BusinessHour(
                dayOfWeek = DayOfWeek.valueOf(hourDto.dayOfWeek),
                openTime = LocalTime.parse(hourDto.openTime),
                closeTime = LocalTime.parse(hourDto.closeTime)
            )
        }
        
        // 주소 생성
        val address = Address(
            roadAddress = request.roadAddress.toRoadAddress(),
            lotAddress = request.lotAddress.toLotAddress(),
            addressType = request.addressType,
            postalCode = request.postalCode,
            latitude = request.location.lat,
            longitude = request.location.lng
        )

        // 픽업 시간 파싱
        val pickupStartTime = request.pickupStartTime?.let { LocalTime.parse(it) } ?: LocalTime.of(9, 0)
        val pickupEndTime = request.pickupEndTime?.let { LocalTime.parse(it) } ?: LocalTime.of(18, 0)
        
        // Store 객체 생성
        val store = Store(
            id = 0L, // 저장 시 자동 생성됨
            ownerId = request.storeOwnerId,
            name = request.name,
            description = request.description,
            address = address,
            businessNumber = request.businessNumber,
            contactNumber = request.contact,
            imageUrl = request.mainImageUrl,
            businessHours = businessHours,
            categories = request.categories,
            status = StoreEnum.StoreStatus.CLOSED, // 기본값은 CLOSED
            pickupStartTime = pickupStartTime,
            pickupEndTime = pickupEndTime,
            pickupAvailableForTomorrow = request.pickupAvailableForTomorrow,
            postalCode = request.postalCode,
            createdAt = ZonedDateTime.now(),
            updatedAt = ZonedDateTime.now(),
            deletedAt = null
        )
        
        val savedStore = storePersistence.save(store)
        return StoreDto.CreateResponse.from(savedStore)
    }
    
    override suspend fun updateStore(id: Long, request: StoreDto.UpdateRequest): StoreDto.Response {
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
        val address = if (request.roadAddress != null && request.lotAddress != null && request.addressType != null && request.location != null) {
            Address(
                roadAddress = request.roadAddress.toRoadAddress(),
                lotAddress = request.lotAddress.toLotAddress(),
                addressType = request.addressType,
                postalCode = request.postalCode,
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
            categories = request.categories,
            pickupStartTime = pickupStartTime,
            pickupEndTime = pickupEndTime,
            pickupAvailableForTomorrow = request.pickupAvailableForTomorrow,
            postalCode = request.postalCode
        )
        
        val savedStore = storePersistence.save(updatedStore)
        return StoreDto.Response.from(savedStore)
    }
    
    override suspend fun updateStoreStatus(id: Long, request: StoreDto.StatusUpdateRequest): StoreDto.Response {
        val existingStore = storePersistence.findById(id) ?: throw StoreException.StoreNotFound(id)
        
        val updatedStore = existingStore.updateStatus(request.status)
        
        val savedStore = storePersistence.save(updatedStore)
        return StoreDto.Response.from(savedStore)
    }
    
    override suspend fun updateStorePickupInfo(id: Long, request: StoreDto.PickupInfoUpdateRequest): StoreDto.Response {
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
        return StoreDto.Response.from(savedStore)
    }
    
    override suspend fun getStoreById(id: Long): StoreDto.Response {
        val store = storePersistence.findById(id) ?: throw StoreException.StoreNotFound(id)
        return StoreDto.Response.from(store)
    }
    
    override suspend fun searchStoresByName(name: String, limit: Int, offset: Int): List<StoreDto.SummaryResponse> {
        val stores = storePersistence.findByNameContaining(name, limit, offset)
        return stores.map { StoreDto.SummaryResponse.from(it) }
    }
    
    override suspend fun findNearbyStores(
        location: LocationDto, 
        radius: Double, 
        limit: Int, 
        offset: Int
    ): List<StoreDto.SummaryResponse> {
        // 사용자 위치 기반 주소 생성
        val address = Address(
            roadAddress = RoadAddress("", "", null),
            lotAddress = LotAddress("", "", null),
            addressType = StoreEnum.AddressType.ROAD,
            postalCode = null,
            latitude = location.lat,
            longitude = location.lng
        )
        
        val stores = storePersistence.findNearby(address, radius, limit, offset)
        return stores.map { store -> 
            StoreDto.SummaryResponse.from(store, location) 
        }
    }
    
    override suspend fun findStoresByCategory(category: String, limit: Int, offset: Int): List<StoreDto.SummaryResponse> {
        val stores = storePersistence.findByCategory(category, limit, offset)
        return stores.map { StoreDto.SummaryResponse.from(it) }
    }
    
    override suspend fun findOpenStores(limit: Int, offset: Int): List<StoreDto.SummaryResponse> {
        val stores = storePersistence.findOpenStores(limit, offset)
        return stores.map { StoreDto.SummaryResponse.from(it) }
    }
    
    override suspend fun findAvailableForPickupStores(limit: Int, offset: Int): List<StoreDto.SummaryResponse> {
        val stores = storePersistence.findOpenStores(limit, offset)
            .filter { it.isAvailableForPickup(ZonedDateTime.now()) }
            
        return stores.map { StoreDto.SummaryResponse.from(it) }
    }
    
    override suspend fun findAvailableForTomorrowStores(limit: Int, offset: Int): List<StoreDto.SummaryResponse> {
        val tomorrow = ZonedDateTime.now().plusDays(1)
        val stores = storePersistence.findOpenStores(limit, offset)
            .filter { it.isAvailableForPickup(tomorrow) }
            
        return stores.map { StoreDto.SummaryResponse.from(it) }
    }
    
    override suspend fun findAvailableForDateStores(targetDate: ZonedDateTime, limit: Int, offset: Int): List<StoreDto.SummaryResponse> {
        val stores = storePersistence.findOpenStores(limit, offset)
            .filter { it.isAvailableForPickup(targetDate) }
        
        return stores.map { StoreDto.SummaryResponse.from(it) }
    }
    
    override suspend fun searchStores(request: StoreDto.SearchRequest): List<StoreDto.SummaryResponse> {
        val stores = when {
            // 위치 기반 검색
            request.location != null -> {
                val address = Address(
                    roadAddress = RoadAddress("", "", null),
                    lotAddress = LotAddress("", "", null),
                    addressType = StoreEnum.AddressType.ROAD,
                    postalCode = null,
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
                StoreDto.SummaryResponse.from(store, loc)
            } ?: StoreDto.SummaryResponse.from(store)
        }
    }
    
    override suspend fun getStoreByOwnerId(ownerId: String): StoreDto.Response? {
        val store = storePersistence.findByOwnerId(ownerId) ?: return null
        return StoreDto.Response.from(store)
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
    private fun validateCreateRequest(request: StoreDto.CreateRequest) {
        requireNotNull(request.name) { "매장 이름은 필수입니다." }
        requireNotNull(request.roadAddress) { "도로명 주소는 필수입니다." }
        requireNotNull(request.lotAddress) { "지번 주소는 필수입니다." }
        requireNotNull(request.contact) { "매장 전화번호는 필수입니다." }
        requireNotNull(request.storeOwnerId) { "매장 소유자 ID는 필수입니다." }
        
        if (request.businessHours.isEmpty()) {
            throw IllegalArgumentException("영업 시간은 적어도 하나 이상 필요합니다.")
        }
    }
}