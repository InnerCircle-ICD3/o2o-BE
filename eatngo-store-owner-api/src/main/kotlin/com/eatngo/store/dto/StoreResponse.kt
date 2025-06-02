package com.eatngo.store.dto

import java.time.LocalDateTime
import java.time.LocalTime


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
    val businessHours: List<BusinessHourDto>,
    val address: AddressDto,
    val pickupDay: String?,
    val todayPickupStartTime: LocalTime?,
    val todayPickupEndTime: LocalTime?,
    val status: String,
    val ratingAverage: Double,
    val ratingCount: Int,
    val foodCategory: List<String>?,
    val storeCategory: List<String>?
) {
    companion object {
        fun fromStoreDto(storeDto: StoreDto): StoreDetailResponse {
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