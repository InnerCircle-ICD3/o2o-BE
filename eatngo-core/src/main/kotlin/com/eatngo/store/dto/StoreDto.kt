package com.eatngo.store.dto

import com.eatngo.store.constant.StoreEnum
import com.eatngo.store.domain.*
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


/**
 * 상점 DTO
 */
data class StoreDto(
    val storeId: Long,
    val name: String,
    val roadAddress: RoadAddressDto,
    val legalAddress: LegalAddressDto,
    val adminAddress: AdminAddressDto? = null,
    val location: LocationDto,
    val businessNumber: String,
    val businessHours: List<BusinessHourDto>,
    val contact: String,
    val description: String,
    val pickupStartTime: String,  // 픽업 시작 시간 (HH:mm)
    val pickupEndTime: String,    // 픽업 종료 시간 (HH:mm)
    val pickupAvailableForTomorrow: Boolean, // 내일 픽업 가능 여부
    val mainImageUrl: String?,
    val status: StoreEnum.StoreStatus,
    val isAvailableForPickup: Boolean, // 현재 픽업 가능한지 여부
    val categories: List<String> = emptyList(),
    val ratingAverage: Double = 0.0, // 평균 별점
    val ratingCount: Int = 0,       // 별점 개수
) {
    companion object {
        fun from(store: Store, nowTime: ZonedDateTime = ZonedDateTime.now()): StoreDto {
            return store.toResponseDto(nowTime)
        }
    }
}


/**
 * 상점 요약 DTO (목록 조회용)
 */
data class StoreSummary(
    val storeId: Long,
    val name: String,
    val mainImageUrl: String?,
    val status: StoreEnum.StoreStatus,
    val isAvailableForPickup: Boolean, // 현재 픽업 가능한지 여부
    val pickupAvailableForTomorrow: Boolean, // 내일 픽업 가능 여부
    val distance: Double? = null
) {
    companion object {
        fun from(store: Store, userLocation: LocationDto? = null): StoreSummary {
            return store.toSummaryDto(userLocation)
        }
    }
}

data class StoreDetailResponse(
    val store: StoreDto,
    val subscribed: Boolean,  // 구독 여부
    val subscriptionCount: Int? = null // 선택적 추가 데이터
)

/**
 * 내가 구독한 매장 목록 응답 DTO
 */
data class SubscribedStoresResponse(
    val userId: Long,
    val subscribedMarkets: List<StoreSummary>
)


/**
 * 도로명 주소 DTO - 도메인 모델과 동일한 구조
 */
data class RoadAddressDto(
    val fullAddress: String,
    val zoneNo: String,
    val buildingName: String? = null
) {
    fun toDomain(): RoadAddress = RoadAddress(
        fullAddress = fullAddress,
        zoneNo = zoneNo,
        buildingName = buildingName
    )

    companion object {
        fun from(roadAddress: RoadAddress): RoadAddressDto {
            return RoadAddressDto(
                fullAddress = roadAddress.fullAddress,
                zoneNo = roadAddress.zoneNo,
                buildingName = roadAddress.buildingName
            )
        }
    }
}

/**
 * 법정동 주소 DTO - 도메인 모델과 동일한 구조
 */
data class LegalAddressDto(
    val fullAddress: String,
    val mainAddressNo: String,
    val subAddressNo: String? = null
) {
    fun toDomain(): LegalAddress = LegalAddress(
        fullAddress = fullAddress,
        mainAddressNo = mainAddressNo,
        subAddressNo = subAddressNo
    )

    companion object {
        fun from(legalAddress: LegalAddress): LegalAddressDto {
            return LegalAddressDto(
                fullAddress = legalAddress.fullAddress,
                mainAddressNo = legalAddress.mainAddressNo,
                subAddressNo = legalAddress.subAddressNo
            )
        }
    }
}

/**
 * 행정동 주소 DTO - 도메인 모델과 동일한 구조
 */
data class AdminAddressDto(
    val fullAddress: String? = null
) {
    fun toDomain(): AdminAddress = AdminAddress(
        fullAddress = fullAddress,
    )

    companion object {
        fun from(adminAddress: AdminAddress?): AdminAddressDto {
            return AdminAddressDto(
                fullAddress = adminAddress?.fullAddress
            )
        }
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
    companion object {
        fun from(businessHour: BusinessHour): BusinessHourDto {
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            return BusinessHourDto(
                dayOfWeek = businessHour.dayOfWeek.name,
                openTime = businessHour.openTime.format(timeFormatter),
                closeTime = businessHour.closeTime.format(timeFormatter)
            )
        }
    }
}
 