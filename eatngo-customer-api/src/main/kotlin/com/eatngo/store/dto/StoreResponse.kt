package com.eatngo.store.dto

import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * 매장 카드뷰 내용 반환을 위해 사용하는 리스폰스
 */
data class StoreResponse(
    val id: Long,
    val name: String,
    val mainImageUrl: String?,
    val status: String,
    val pickupStartTime: LocalTime?,
    val pickupEndTime: LocalTime?,
    val pickupDay: String?,
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
                pickupStartTime = storeDto.pickUpInfo.pickupStartTime!!,
                pickupEndTime = storeDto.pickUpInfo.pickupEndTime!!,
                pickupDay = storeDto.pickUpInfo.pickupDay?.name,
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
    val businessNumber: Long,
    val businessHours: List<BusinessHourResponse>,
    val latitude: Double?,
    val longitude: Double?,
    val pickupStartTime: LocalTime?,
    val pickupEndTime: LocalTime?,
    val pickupDay: String?,
    val status: String,
    val ratingAverage: Double,
    val ratingCount: Int,
    val foodCategory: List<String>?,
    val storeCategory: List<String>?,
) {
    companion object {
        fun from(storeDto: StoreDto): StoreDetailResponse {
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            return StoreDetailResponse(
                id = storeDto.storeId,
                name = storeDto.name,
                mainImageUrl = storeDto.imageUrl,
                contact = storeDto.contactNumber ?: "",
                description = storeDto.description ?: "",
                businessNumber = storeDto.businessNumber,
                businessHours = storeDto.businessHours.map { hour ->
                    BusinessHourResponse(
                        dayOfWeek = hour.dayOfWeek.name,
                        openTime = hour.openTime.format(timeFormatter),
                        closeTime = hour.closeTime.format(timeFormatter),
                    )
                },
                latitude = storeDto.address.coordinate.latitude,
                longitude = storeDto.address.coordinate.longitude,
                pickupStartTime = storeDto.pickUpInfo.pickupStartTime,
                pickupEndTime = storeDto.pickUpInfo.pickupEndTime,
                pickupDay = storeDto.pickUpInfo.pickupDay?.name,
                status = storeDto.status.name,
                ratingAverage = storeDto.reviewInfo.ratingAverage,
                ratingCount = storeDto.reviewInfo.ratingCount,
                foodCategory = storeDto.storeCategoryInfo.foodCategory,
                storeCategory = storeDto.storeCategoryInfo.storeCategory!!,
            )
        }
    }
}

data class BusinessHourResponse(
    val dayOfWeek: String,  // 요일 (MONDAY, TUESDAY, ...)
    val openTime: String,   // 영업 시작 시간 (HH:MM 형식)
    val closeTime: String,  // 영업 종료 시간 (HH:MM 형식)
)