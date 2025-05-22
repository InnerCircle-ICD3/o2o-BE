package com.eatngo.store.dto

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


/**
 * 매장 CUD 시 반환하는 간단한 응답
 */
data class StoreCUDResponse(val storeId: Long, val actionTime: LocalDateTime? = LocalDateTime.now())

/**
 * 점주가 매장을 관리할 때 상세정보 반환을 위해 사용하는 리스폰스
 * TODO: 추후 백오피스 UI 확정되면 수정 가능성 있음
 */
data class StoreDetailResponse(
    val id: Long,
    val name: String,
    val mainImageUrl: String?,
    val contact: String,
    val description: String,
    val businessNumber: String,
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
    val storeCategory: List<String>?
) {
    companion object {
        fun fromStoreDto(storeDto: StoreDto): StoreDetailResponse {
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
                storeCategory = storeDto.storeCategoryInfo.storeCategory
            )
        }
    }
}

data class BusinessHourResponse(
    val dayOfWeek: String,  // 요일 (MONDAY, TUESDAY, ...)
    val openTime: String,   // 영업 시작 시간 (HH:MM 형식)
    val closeTime: String,  // 영업 종료 시간 (HH:MM 형식)
)