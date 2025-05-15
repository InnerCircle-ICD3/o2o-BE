package com.eatngo.store.dto

import com.eatngo.store.constant.StoreEnum
import com.eatngo.store.domain.*
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZonedDateTime

/**
 * 상점 DTO 클래스들
 */
object StoreDto {
    /**
     * 상점 생성 요청 DTO
     */
    data class CreateRequest(
        val name: String,
        val storeOwnerId: String,
        val roadAddress: RoadAddressDto,
        val lotAddress: LotAddressDto,
        val addressType: StoreEnum.AddressType,
        val location: LocationDto,
        val businessNumber: String,
        val businessHours: List<BusinessHourDto>,
        val contact: String,
        val description: String,
        val postalCode: String,
        val pickupStartTime: String,  // 픽업 시작 시간 (HH:mm) - 필수
        val pickupEndTime: String,    // 픽업 종료 시간 (HH:mm) - 필수
        val pickupAvailableForTomorrow: Boolean = false, // 내일 픽업 가능 여부
        val mainImageUrl: String? = null,
        val categories: List<String> = emptyList()
    )
    
    /**
     * 상점 생성 응답 DTO
     */
    data class CreateResponse(
        val storeId: Long,
        val message: String = "매장 등록 성공"
    ) {
        companion object {
            fun from(store: Store): CreateResponse {
                return CreateResponse(
                    storeId = store.id
                )
            }
        }
    }
    
    /**
     * 상점 수정 요청 DTO
     */
    data class UpdateRequest(
        val name: String? = null,
        val roadAddress: RoadAddressDto? = null,
        val lotAddress: LotAddressDto? = null,
        val addressType: StoreEnum.AddressType? = null,
        val location: LocationDto? = null,
        val businessNumber: String? = null,
        val businessHours: List<BusinessHourDto>? = null,
        val contact: String? = null,
        val description: String? = null,
        val postalCode: String? = null,
        val pickupStartTime: String? = null,  // 픽업 시작 시간 (HH:mm)
        val pickupEndTime: String? = null,    // 픽업 종료 시간 (HH:mm)
        val pickupAvailableForTomorrow: Boolean? = null, // 내일 픽업 가능 여부
        val mainImageUrl: String? = null,
        val categories: List<String>? = null
    )
    
    /**
     * 상점 상태 변경 요청 DTO
     */
    data class StatusUpdateRequest(
        val status: StoreEnum.StoreStatus
    )
    
    /**
     * 상점 픽업 정보 변경 요청 DTO
     */
    data class PickupInfoUpdateRequest(
        val pickupStartTime: String? = null,  // 픽업 시작 시간 (HH:mm)
        val pickupEndTime: String? = null,    // 픽업 종료 시간 (HH:mm)
        val pickupAvailableForTomorrow: Boolean? = null // 내일 픽업 가능 여부
    )
    
    /**
     * 상점 검색 요청 DTO
     */
    data class SearchRequest(
        val keyword: String? = null,
        val location: LocationDto? = null,
        val radius: Double? = null,
        val category: String? = null,
        val onlyOpen: Boolean? = null,        // 영업중인 매장만 필터링 (status가 OPEN인 매장)
        val availableForPickup: Boolean? = null, // 현재 픽업 가능한 매장만 필터링
        val availableForTomorrow: Boolean? = null, // 내일 픽업 가능한 매장만 필터링
        val targetDate: ZonedDateTime? = null, // 특정 날짜에 픽업 가능한 매장 필터링
        val limit: Int = 10,
        val offset: Int = 0
    )
    
    /**
     * 상점 응답 DTO
     */
    data class Response(
        val storeId: Long,
        val name: String,
        val roadAddress: RoadAddressDto,
        val lotAddress: LotAddressDto,
        val addressType: StoreEnum.AddressType,
        val location: LocationDto,
        val businessNumber: String,
        val businessHours: List<BusinessHourDto>,
        val contact: String,
        val description: String,
        val postalCode: String?,
        val pickupStartTime: String,  // 픽업 시작 시간 (HH:mm)
        val pickupEndTime: String,    // 픽업 종료 시간 (HH:mm)
        val pickupAvailableForTomorrow: Boolean, // 내일 픽업 가능 여부
        val mainImageUrl: String?,
        val status: StoreEnum.StoreStatus,
        val isAvailableForPickup: Boolean, // 현재 픽업 가능한지 여부
        val categories: List<String> = emptyList()
    ) {
        companion object {
            fun from(store: Store): Response {
                val now = ZonedDateTime.now()
                return Response(
                    storeId = store.id,
                    name = store.name,
                    roadAddress = RoadAddressDto(
                        addressName = store.address.roadAddress.addressName,
                        zoneNo = store.address.roadAddress.zoneNo,
                        buildingName = store.address.roadAddress.buildingName
                    ),
                    lotAddress = LotAddressDto(
                        addressName = store.address.lotAddress.addressName,
                        mainAddressNo = store.address.lotAddress.mainAddressNo,
                        subAddressNo = store.address.lotAddress.subAddressNo
                    ),
                    addressType = store.address.addressType,
                    location = LocationDto(
                        lat = store.address.latitude,
                        lng = store.address.longitude
                    ),
                    businessNumber = store.businessNumber,
                    businessHours = store.businessHours.map { 
                        BusinessHourDto(
                            dayOfWeek = it.dayOfWeek.toString(),
                            openTime = it.openTime.toString(),
                            closeTime = it.closeTime.toString()
                        )
                    },
                    contact = store.contactNumber,
                    description = store.description,
                    postalCode = store.address.postalCode,
                    pickupStartTime = store.pickupStartTime.toString(),
                    pickupEndTime = store.pickupEndTime.toString(),
                    pickupAvailableForTomorrow = store.pickupAvailableForTomorrow,
                    mainImageUrl = store.imageUrl,
                    status = store.status,
                    isAvailableForPickup = store.isAvailableForPickup(now),
                    categories = store.categories
                )
            }
        }
    }
    
    /**
     * 상점 요약 응답 DTO (목록 조회용)
     */
    data class SummaryResponse(
        val storeId: Long,
        val name: String,
        val mainImageUrl: String?,
        val status: StoreEnum.StoreStatus,
        val isAvailableForPickup: Boolean, // 현재 픽업 가능한지 여부
        val pickupAvailableForTomorrow: Boolean, // 내일 픽업 가능 여부
        val distance: Double? = null
    ) {
        companion object {
            fun from(store: Store, userLocation: LocationDto? = null): SummaryResponse {
                val now = ZonedDateTime.now()
                val distance = userLocation?.let {
                    val userAddress = Address(
                        roadAddress = RoadAddress("", "", null),
                        lotAddress = LotAddress("", "", null),
                        addressType = StoreEnum.AddressType.ROAD,
                        postalCode = null,
                        latitude = it.lat,
                        longitude = it.lng
                    )
                    store.address.distanceTo(userAddress)
                }
                
                return SummaryResponse(
                    storeId = store.id,
                    name = store.name,
                    mainImageUrl = store.imageUrl,
                    status = store.status,
                    isAvailableForPickup = store.isAvailableForPickup(now),
                    pickupAvailableForTomorrow = store.pickupAvailableForTomorrow,
                    distance = distance
                )
            }
        }
    }
    
    /**
     * 내가 구독한 매장 목록 응답 DTO
     */
    data class SubscribedStoresResponse(
        val userId: Long,
        val subscribedMarkets: List<SummaryResponse>
    )
}

/**
 * 도로명 주소 DTO
 */
data class RoadAddressDto(
    val addressName: String,
    val zoneNo: String,
    val buildingName: String?
) {
    fun toRoadAddress(): RoadAddress {
        return RoadAddress(
            addressName = addressName,
            zoneNo = zoneNo,
            buildingName = buildingName
        )
    }
}

/**
 * 지번 주소 DTO
 */
data class LotAddressDto(
    val addressName: String,
    val mainAddressNo: String,
    val subAddressNo: String?
) {
    fun toLotAddress(): LotAddress {
        return LotAddress(
            addressName = addressName,
            mainAddressNo = mainAddressNo,
            subAddressNo = subAddressNo
        )
    }
}

/**
 * 위치 정보 DTO
 */
data class LocationDto(
    val lat: Double,
    val lng: Double
)

/**
 * 영업시간 정보 DTO
 */
data class BusinessHourDto(
    val dayOfWeek: String,
    val openTime: String,
    val closeTime: String
) {
    fun toBusinessHour(): BusinessHour {
        return BusinessHour(
            dayOfWeek = DayOfWeek.valueOf(dayOfWeek),
            openTime = LocalTime.parse(openTime),
            closeTime = LocalTime.parse(closeTime)
        )
    }
}
 