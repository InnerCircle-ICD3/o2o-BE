package com.eatngo.store.dto

import com.eatngo.common.constant.StoreEnum
import java.time.LocalTime

/**
 * 매장 생성 요청용 dto
 */

data class StoreCreateRequest(
    // 기본 정보
    val name: String,
    val businessNumber: String,

    // 주소 정보 (Flat 구조)
    val roadFullAddress: String,
    val roadZoneNo: String,
    val roadBuildingName: String? = null,
    val legalFullAddress: String? = null,
    val legalMainAddressNo: String? = null,
    val legalSubAddressNo: String? = null,
    val adminFullAddress: String? = null,

    // 위치 정보
    val latitude: Double? = null,
    val longitude: Double? = null,

    // 운영 정보
    val pickupStartTime: LocalTime,
    val pickupEndTime: LocalTime,
    val pickupDay: String,

    // 부가 정보
    val businessHours: List<BusinessHourDto>? = null,
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
                roadAddress = RoadAddressDto(
                    fullAddress = this.roadFullAddress,
                    zoneNo = this.roadZoneNo
                ),
                legalAddress = if (this.legalFullAddress != null) {
                    LegalAddressDto(this.legalFullAddress)
                } else null,
                adminAddress = this.adminFullAddress?.let { AdminAddressDto(it) },
                coordinate = CoordinateDto(
                    latitude = this.latitude ?: 0.0,
                    longitude = this.longitude ?: 0.0
                )
            ),
            businessNumber = this.businessNumber,
            businessHours = this.businessHours,
            contactNumber = this.contact,
            description = this.description,
            imageUrl = this.mainImageUrl,
            pickUpInfo = PickUpInfoDto(
                pickupStartTime = this.pickupStartTime,
                pickupEndTime = this.pickupEndTime,
                pickupDay = StoreEnum.PickupDay.valueOf(this.pickupDay.uppercase())
            ),
            storeCategoryInfo = StoreCategoryInfoDto(
                storeCategory = this.storeCategory,
                foodCategory = this.foodCategory
            )
        )
    }
}