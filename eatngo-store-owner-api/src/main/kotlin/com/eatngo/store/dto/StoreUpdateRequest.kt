package com.eatngo.store.dto

import com.eatngo.common.constant.StoreEnum
import java.time.DayOfWeek
import java.time.LocalTime

/**
 * 매장 수정 요청용 dto
 */

data class StoreUpdateRequest(
    val name: String? = null,
    val businessNumber: String? = null,

    // 주소 정보 (flat)
    val roadNameAddress: String? = null,
    val lotNumberAddress: String? = null,
    val buildingName: String? = null,
    val zipCode: String? = null,
    val region1DepthName: String? = null,
    val region2DepthName: String? = null,
    val region3DepthName: String? = null,

    // 위치 정보
    val latitude: Double? = null,
    val longitude: Double? = null,

    // 운영 정보
    val pickupStartTime: String? = null,
    val pickupEndTime: String? = null,
    val pickupDay: String? = null,

    // 부가 정보
    val businessHours: List<Map<String, Any>>? = null,
    val contact: String? = null,
    val description: String? = null,
    val mainImageUrl: String? = null,
    val storeCategory: List<String>? = null,
    val foodCategory: List<String>? = null
) {
    fun toStoreUpdateDto(storeOwnerId: Long): StoreUpdateDto {
        val addressDto = if (
            roadNameAddress != null && zipCode != null &&
            latitude != null && longitude != null
        ) {
            AddressDto(
                roadNameAddress = roadNameAddress,
                lotNumberAddress = lotNumberAddress,
                buildingName = buildingName,
                zipCode = zipCode,
                region1DepthName = region1DepthName,
                region2DepthName = region2DepthName,
                region3DepthName = region3DepthName,
                coordinate = CoordinateDto(
                    latitude = latitude,
                    longitude = longitude
                )
            )
        } else null

        val pickUpInfoDto = if (pickupStartTime != null && pickupEndTime != null && pickupDay != null) {
            PickUpInfoDto(
                pickupStartTime = this.pickupStartTime.let { LocalTime.parse(it) },
                pickupEndTime = this.pickupEndTime.let { LocalTime.parse(it) },
                pickupDay = StoreEnum.PickupDay.valueOf(this.pickupDay.uppercase())
            )
        } else null

        // 카테고리 정보가 있는 경우에만 StoreCategoryInfoDto 생성
        val storeCategoryInfoDto = if (storeCategory != null || foodCategory != null) {
            StoreCategoryInfoDto(
                storeCategory = storeCategory,
                foodCategory = foodCategory
            )
        } else null

        return StoreUpdateDto(
            storeOwnerId = storeOwnerId,
            name = name,
            address = addressDto,
            businessHours = this.businessHours?.map { map ->
                BusinessHourDto(
                    dayOfWeek = DayOfWeek.valueOf((map["dayOfWeek"] as String).uppercase()),
                    openTime = LocalTime.parse(map["openTime"] as String),
                    closeTime = LocalTime.parse(map["closeTime"] as String)
                )
            },
            contactNumber = contact,
            description = description,
            mainImageUrl = mainImageUrl,
            pickUpInfo = pickUpInfoDto,
            storeCategoryInfo = storeCategoryInfoDto
        )
    }
}

data class StoreStatusUpdateRequest(val status: String)
