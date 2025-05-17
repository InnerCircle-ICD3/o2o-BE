package com.eatngo.store.dto

import com.eatngo.store.constant.StoreEnum
import com.eatngo.store.domain.*
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


/**
 * 상점 수정 요청 DTO
 */
data class StoreUpdateDto(
    val name: String? = null,
    val roadAddress: RoadAddressDto? = null,
    val legalAddress: LegalAddressDto? = null,
    val adminAddress: AdminAddressDto? = null,
    val location: LocationDto? = null,
    val businessNumber: String? = null,
    val businessHours: List<BusinessHourDto>? = null,
    val contact: String? = null,
    val description: String? = null,
    val pickupStartTime: String? = null,  // 픽업 시작 시간 (HH:mm)
    val pickupEndTime: String? = null,    // 픽업 종료 시간 (HH:mm)
    val pickupAvailableForTomorrow: Boolean? = null, // 내일 픽업 가능 여부
    val mainImageUrl: String? = null,
    val categories: List<String>? = null
) {
    fun toUpdateParams(): Store.() -> Store {
        return {
            update(
                name = name,
                description = description,
                address = constructUpdatedAddress(),
                businessNumber = businessNumber,
                contactNumber = contact,
                imageUrl = mainImageUrl,
                businessHours = businessHours?.map { dto ->
                    BusinessHour(
                        dayOfWeek = DayOfWeek.valueOf(dto?.dayOfWeek.toString()),
                        openTime = LocalTime.parse(dto?.openTime.toString()),
                        closeTime = LocalTime.parse(dto?.closeTime.toString())
                    )
                },
                categories = categories?.map { it },
                pickupStartTime = pickupStartTime,
                pickupEndTime = pickupEndTime,
                pickupAvailableForTomorrow = pickupAvailableForTomorrow
            )
        }
    }

    private fun constructUpdatedAddress(): Address? {
        // 주소 관련 필드가 모두 null이면 주소 업데이트 안함
        if (roadAddress == null && legalAddress == null && adminAddress == null && location == null) {
            return null
        }

        // 기존 주소가 필요하지만 여기서는 접근할 수 없으므로
        // 우선은 받은 값들로만 Address 생성
        // 추후 기존 주소 정보를 활용하여 합쳐야 함
        return null
    }
}

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
) {
    fun toUpdateFunction(): Store.() -> Store {
        return {
            updatePickupInfo(
                startTime = pickupStartTime,
                endTime = pickupEndTime,
                availableForTomorrow = pickupAvailableForTomorrow
            )
        }
    }
}