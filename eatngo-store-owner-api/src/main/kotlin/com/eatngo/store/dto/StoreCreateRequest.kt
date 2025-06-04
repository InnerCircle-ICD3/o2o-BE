package com.eatngo.store.dto

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.docs.StoreCreateRequestDocs
import io.swagger.v3.oas.annotations.media.Schema
import java.time.DayOfWeek
import java.time.LocalTime

/**
 * 매장 생성 요청용 dto
 */

data class StoreCreateRequest(
    // 기본 정보
   override val name: String,
   override val businessNumber: String,

    // 주소 정보 (Flat 구조)
   override val roadNameAddress: String? = null,
   override val lotNumberAddress: String? = null,
   override val buildingName: String? = null,
   override val zipCode: String? = null,
   override val region1DepthName: String? = null,
   override val region2DepthName: String? = null,
   override val region3DepthName: String? = null,

    // 위치 정보
   override val latitude: Double? = null,
   override val longitude: Double? = null,

    // 운영 정보
   override val pickupDay: String? = null,
   override val businessHours: List<BusinessHourDto>? = null,

    // 부가 정보
   override val contact: String? = null,
   override val description: String? = null,
   override val mainImageUrl: String? = null,
   override val storeCategory: List<String>? = null,
   override val foodCategory: List<String>?  = null,
): StoreCreateRequestDocs {
    fun toStoreCreateDto(storeOwnerId: Long): StoreCreateDto {
        return StoreCreateDto(
            storeOwnerId = storeOwnerId,
            name = this.name,
            address = AddressDto(
                roadNameAddress = this.roadNameAddress,
                lotNumberAddress = this.lotNumberAddress,
                buildingName = this.buildingName,
                zipCode = this.zipCode,
                region1DepthName = this.region1DepthName,
                region2DepthName = this.region2DepthName,
                region3DepthName = this.region3DepthName,
                coordinate = CoordinateDto(
                    latitude = this.latitude ?: 0.0,
                    longitude = this.longitude ?: 0.0
                )
            ),
            businessNumber = this.businessNumber,
            businessHours = this.businessHours?.map { hour ->
                BusinessHourDto(
                    dayOfWeek = hour.dayOfWeek,
                    openTime = hour.openTime,
                    closeTime = hour.closeTime,
                )
            },
            contactNumber = this.contact,
            description = this.description,
            imageUrl = this.mainImageUrl,
            pickUpDay = pickupDay,
            storeCategoryInfo = StoreCategoryInfoDto(
                storeCategory = this.storeCategory,
                foodCategory = this.foodCategory
            )
        )
    }
}