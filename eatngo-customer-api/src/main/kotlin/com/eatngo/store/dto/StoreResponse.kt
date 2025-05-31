package com.eatngo.store.dto

import com.eatngo.common.constant.StoreEnum
import java.time.LocalTime

/**
 * 매장 카드뷰 내용 반환을 위해 사용하는 리스폰스
 */
data class StoreResponse(
    val id: Long,
    val name: String,
    val mainImageUrl: String?,
    val status: String,
    val pickupDay: String?,
    val todayPickupStartTime: LocalTime?,
    val todayPickupEndTime: LocalTime?,
    val distance: Double?,
    val ratingAverage: Double,
    val ratingCount: Int,
    val foodCategory: List<String>?,
) {
    companion object {
        fun from(storeDto: StoreDto, distance: Double?): StoreResponse {
            return StoreResponse(
                id = storeDto.storeId,
                name = storeDto.name,
                mainImageUrl = storeDto.imageUrl,
                status = storeDto.status.name,
                todayPickupStartTime = storeDto.todayPickupStartTime,
                todayPickupEndTime = storeDto.todayPickupEndTime,
                pickupDay = storeDto.pickUpDay,
                distance = distance,
                ratingAverage = storeDto.reviewInfo.ratingAverage,
                ratingCount = storeDto.reviewInfo.ratingCount,
                foodCategory = storeDto.storeCategoryInfo.foodCategory
            )
        }
    }
}

/**
 * 매장 카드 클릭 시 상세정보 반환을 위해 사용하는 리스폰스
 */
data class StoreDetailResponse(
    val id: Long,
    val name: String,
    val mainImageUrl: String?,
    val contact: String,
    val description: String,
    val businessNumber: String,
    val businessHours: List<BusinessHourDto>,
    val address: AddressDto,
    val pickupDay: String?,
    val todayPickupStartTime: LocalTime?,
    val todayPickupEndTime: LocalTime?,
    val status: String,
    val ratingAverage: Double,
    val ratingCount: Int,
    val foodCategory: List<String>?,
    val storeCategory: List<String>?,
) {
    companion object {
        fun from(storeDto: StoreDto): StoreDetailResponse {
            return StoreDetailResponse(
                id = storeDto.storeId,
                name = storeDto.name,
                mainImageUrl = storeDto.imageUrl,
                contact = storeDto.contactNumber ?: "",
                description = storeDto.description ?: "",
                businessNumber = storeDto.businessNumber,
                businessHours = storeDto.businessHours.map { hour ->
                    BusinessHourDto(
                        dayOfWeek = hour.dayOfWeek,
                        openTime = hour.openTime,
                        closeTime = hour.closeTime,
                    )
                },
                address = AddressDto(
                    roadNameAddress = storeDto.address.roadNameAddress,
                    lotNumberAddress = storeDto.address.lotNumberAddress,
                    buildingName = storeDto.address.buildingName,
                    zipCode = storeDto.address.zipCode,
                    region1DepthName = storeDto.address.region1DepthName,
                    region2DepthName = storeDto.address.region2DepthName,
                    region3DepthName = storeDto.address.region3DepthName,
                    coordinate = CoordinateDto(
                        latitude = storeDto.address.coordinate.latitude,
                        longitude = storeDto.address.coordinate.longitude
                    )
                ),
                todayPickupStartTime = storeDto.todayPickupStartTime,
                todayPickupEndTime = storeDto.todayPickupEndTime,
                pickupDay = storeDto.pickUpDay,
                status = storeDto.status.name,
                ratingAverage = storeDto.reviewInfo.ratingAverage,
                ratingCount = storeDto.reviewInfo.ratingCount,
                foodCategory = storeDto.storeCategoryInfo.foodCategory,
                storeCategory = storeDto.storeCategoryInfo.storeCategory
            )
        }
    }
}