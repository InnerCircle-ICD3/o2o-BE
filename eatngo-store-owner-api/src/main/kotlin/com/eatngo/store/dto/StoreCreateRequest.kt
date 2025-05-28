package com.eatngo.store.dto

import com.eatngo.common.constant.StoreEnum
import java.time.DayOfWeek
import java.time.LocalTime

/**
 * 매장 생성 요청용 dto
 */

data class StoreCreateRequest(
    // 기본 정보
    val name: String,
    val businessNumber: Long,

    // 주소 정보 (Flat 구조)
    val roadNameAddress: String? = null,
    val zipCode: String? = null,
    val lotNumberAddress: String? = null,

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
    val foodCategory: List<String>?  = null,
) {
    fun toStoreCreateDto(storeOwnerId: Long): StoreCreateDto {
        return StoreCreateDto(
            storeOwnerId = storeOwnerId,
            name = this.name,
            address = AddressDto(
                roadNameAddress = this.roadNameAddress,
                lotNumberAddress = this.lotNumberAddress,
                zipCode = this.zipCode,
                coordinate = CoordinateDto(
                    latitude = this.latitude ?: 0.0,
                    longitude = this.longitude ?: 0.0
                )
            ),
            businessNumber = this.businessNumber,
            businessHours = this.businessHours?.map { map ->
                BusinessHourDto(
                    dayOfWeek = DayOfWeek.valueOf((map["dayOfWeek"] as String).uppercase()),
                    openTime = LocalTime.parse(map["openTime"] as String),
                    closeTime = LocalTime.parse(map["closeTime"] as String)
                )
            },
            contactNumber = this.contact,
            description = this.description,
            imageUrl = this.mainImageUrl,
            pickUpInfo = PickUpInfoDto(
                pickupStartTime = this.pickupStartTime?.let { LocalTime.parse(it) },
                pickupEndTime = this.pickupEndTime?.let { LocalTime.parse(it) },
                pickupDay = StoreEnum.PickupDay.valueOf(this.pickupDay!!.uppercase())
            ),
            storeCategoryInfo = StoreCategoryInfoDto(
                storeCategory = this.storeCategory,
                foodCategory = this.foodCategory
            )
        )
    }
}