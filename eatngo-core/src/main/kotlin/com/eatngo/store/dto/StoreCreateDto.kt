package com.eatngo.store.dto

import com.eatngo.store.constant.StoreEnum
import com.eatngo.store.domain.*
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * 상점 생성 요청 DTO
 */
data class StoreCreateDto(
    val name: String,
    val storeOwnerId: String,                     // 백엔드에서 주입
    val roadAddress: RoadAddressDto,
    val legalAddress: LegalAddressDto? = null,
    val adminAddress: AdminAddressDto? = null,
    val location: LocationDto,
    val businessNumber: String,
    val businessHours: List<BusinessHourDto>? = null,
    val contact: String? = null,
    val description: String? = null,
    val pickupStartTime: String,
    val pickupEndTime: String,
    val pickupAvailableForTomorrow: Boolean = false,
    val mainImageUrl: String? = null,
    val categories: List<String> = emptyList()
) {
    fun toDomain(storeId: Long, createdAt: ZonedDateTime = ZonedDateTime.now()): Store {
        val address = Address(
            roadAddress = RoadAddress(
                fullAddress = roadAddress.fullAddress,
                zoneNo = roadAddress.zoneNo,
                buildingName = roadAddress.buildingName
            ),
            legalAddress = LegalAddress(
                fullAddress = legalAddress?.fullAddress!!,
                mainAddressNo = legalAddress.mainAddressNo,
                subAddressNo = legalAddress.subAddressNo
            ),
            adminAddress = AdminAddress(
                fullAddress = adminAddress?.fullAddress,
            ),
            latitude = location.lat,
            longitude = location.lng
        )

        return Store(
            id = storeId,
            storeOwnerId = storeOwnerId,
            name = name,
            description = description,
            address = address,
            businessNumber = businessNumber,
            contactNumber = contact,
            imageUrl = mainImageUrl,
            businessHours = businessHours?.map { dto ->
                BusinessHour(
                    dayOfWeek = DayOfWeek.valueOf(dto.dayOfWeek),
                    openTime = LocalTime.parse(dto.openTime),
                    closeTime = LocalTime.parse(dto.closeTime)
                )
            },
            categories = categories.map { it as String? },
            pickupStartTime = LocalTime.parse(pickupStartTime),
            pickupEndTime = LocalTime.parse(pickupEndTime),
            pickupAvailableForTomorrow = pickupAvailableForTomorrow,
            createdAt = createdAt,
            updatedAt = createdAt
        )
    }
}