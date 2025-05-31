package com.eatngo.store.dto

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.domain.Store
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.LocalDateTime
import kotlin.String


/**
 * 상점 DTO
 */
data class StoreDto(
    val storeId: Long,
    val storeOwnerId: Long,
    val name: String,
    val description: String?,
    val address: AddressDto,
    val businessNumber: String,
    val contactNumber: String?,
    val imageUrl: String?,
    val businessHours: List<BusinessHourDto> = emptyList(),
    val storeCategoryInfo: StoreCategoryInfoDto,
    val status: StoreEnum.StoreStatus,
    val pickUpDay: String,
    val todayPickupStartTime: LocalTime?,
    val todayPickupEndTime: LocalTime?,
    val reviewInfo: ReviewInfoDto,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    var deletedAt: LocalDateTime?,
) {
    companion object {
        fun from(store: Store): StoreDto {
            val today = java.time.LocalDate.now().dayOfWeek
            val todayHour = store.businessHours?.find { it.dayOfWeek == today }
            return StoreDto(
                storeId = store.id,
                storeOwnerId = store.storeOwnerId,
                name = store.name.value,
                description = store.description?.value,
                address = AddressDto(
                    roadNameAddress = store.address.roadNameAddress.value,
                    lotNumberAddress = store.address.lotNumberAddress.value,
                    buildingName = store.address.buildingName,
                    zipCode = store.address.zipCode.value,
                    region1DepthName = store.address.region1DepthName,
                    region2DepthName = store.address.region2DepthName,
                    region3DepthName = store.address.region3DepthName,
                    coordinate = CoordinateDto(
                        latitude = store.address.coordinate.latitude,
                        longitude = store.address.coordinate.longitude
                    )
                ),
                businessNumber = store.businessNumber.value,
                contactNumber = store.contactNumber?.value,
                imageUrl = store.imageUrl?.value,
                businessHours = store.businessHours?.map {
                    BusinessHourDto(it.dayOfWeek, it.openTime, it.closeTime)
                } ?: emptyList(),
                storeCategoryInfo = StoreCategoryInfoDto(
                    storeCategory = store.storeCategoryInfo.storeCategory.map { it.value.name },
                    foodCategory = store.storeCategoryInfo.foodCategory?.map { it.value }
                ),
                status = store.status,
                pickUpDay = store.pickUpDay.pickUpDay.name,
                todayPickupStartTime = todayHour?.openTime,
                todayPickupEndTime = todayHour?.closeTime,
                reviewInfo = ReviewInfoDto(
                    //TODO: 리뷰는 dto에만 포함, 추후 기능 개발되면 서비스에서 값 가져와야 함
                    ratingAverage = 0.0,
                    ratingCount = 0
                ),
                createdAt = store.createdAt,
                updatedAt = store.updatedAt,
                deletedAt = store.deletedAt
            )
        }
    }
}

/**
 * 주소 관련 정보를 담은 DTO
 */
data class AddressDto(
    val roadNameAddress: String? = null,
    val lotNumberAddress: String? = null,
    val buildingName: String? = null,
    val zipCode: String? = null,
    val region1DepthName: String? = null,
    val region2DepthName: String? = null,
    val region3DepthName: String? = null,
    val coordinate: CoordinateDto
)

/**
 * 영업시간 정보를 담은 DTO
 */
data class BusinessHourDto(
    val dayOfWeek: DayOfWeek,
    val openTime: LocalTime,
    val closeTime: LocalTime
)

/**
 *  위도, 경도 좌표 DTO
 */
data class CoordinateDto(
    val latitude: Double? = 0.0,
    val longitude: Double? = 0.0
)

/**
 * 리뷰 정보 DTO
 */
data class ReviewInfoDto(
    val ratingAverage: Double = 0.0,
    val ratingCount: Int = 0,
)

/**
 * 매장의 카테고리 정보(분류와 사용자 입력 카테고리) DTO
 */
data class StoreCategoryInfoDto(
    val storeCategory: List<String>?,
    val foodCategory: List<String>?
)