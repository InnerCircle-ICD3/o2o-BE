package com.eatngo.store.dto.extension

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.domain.Address
import com.eatngo.store.domain.AdminAddress
import com.eatngo.store.domain.BusinessHour
import com.eatngo.store.domain.Coordinate
import com.eatngo.store.domain.LegalAddress
import com.eatngo.store.domain.PickUpInfo
import com.eatngo.store.domain.ReviewInfo
import com.eatngo.store.domain.RoadAddress
import com.eatngo.store.domain.Store
import com.eatngo.store.domain.StoreCategoryInfo
import com.eatngo.store.domain.StoreSubscription
import com.eatngo.store.dto.AddressDto
import com.eatngo.store.dto.AdminAddressDto
import com.eatngo.store.dto.BusinessHourDto
import com.eatngo.store.dto.CoordinateDto
import com.eatngo.store.dto.LegalAddressDto
import com.eatngo.store.dto.PickUpInfoDto
import com.eatngo.store.dto.ReviewInfoDto
import com.eatngo.store.dto.RoadAddressDto
import com.eatngo.store.dto.StoreCategoryInfoDto
import com.eatngo.store.dto.StoreDto
import com.eatngo.store.dto.StoreSubscriptionDto
import com.eatngo.store.vo.FoodCategory
import com.eatngo.store.vo.StoreCategory

/**
 * core 모듈의 dto를 domain으로 매핑
 */
fun BusinessHourDto.toDomain(): BusinessHour = BusinessHour(
    dayOfWeek = this.dayOfWeek,
    openTime = this.openTime,
    closeTime = this.closeTime
)


fun List<BusinessHourDto>.toDomain(): List<BusinessHour> {
    return this.map { it.toDomain() }
}

// RoadAddressDto → RoadAddress
fun RoadAddressDto.toDomain(): RoadAddress = RoadAddress(
    fullAddress = this.fullAddress,
    zoneNo = this.zoneNo
)

// LegalAddressDto → LegalAddress
fun LegalAddressDto.toDomain(): LegalAddress = LegalAddress(
    fullAddress = this.fullAddress
)

// AdminAddressDto → AdminAddress
fun AdminAddressDto.toDomain(): AdminAddress = AdminAddress(
    fullAddress = this.fullAddress
)

// AddressDto -> Address
fun AddressDto.toDomain(): Address {
    return Address(
        roadAddress = this.roadAddress.toDomain(),
        legalAddress = this.legalAddress?.toDomain(),
        adminAddress = this.adminAddress?.toDomain(),
        coordinate = this.coordinate.toDomain(),
    )
}

fun CoordinateDto.toDomain(): Coordinate = Coordinate(
    latitude = this.latitude,
    longitude = this.longitude
)

fun StoreCategoryInfoDto.toDomain(): StoreCategoryInfo {
    return StoreCategoryInfo(
        storeCategory = this.storeCategory.map { StoreCategory.from(it) },
        foodCategory = this.foodCategory?.map { FoodCategory.from(it) }
    )
}

fun PickUpInfoDto.toDomain(): PickUpInfo {
    return PickUpInfo(
        pickupStartTime = this.pickupStartTime,
        pickupEndTime = this.pickupEndTime,
        pickupAvailableForTomorrow = this.pickupAvailableForTomorrow,
    )
}

fun ReviewInfoDto.toDomain(): ReviewInfo {
    return ReviewInfo(
        ratingAverage = this.ratingAverage,
        ratingCount = this.ratingCount,
    )
}

/**
 * core 모듈의 domain을 dto로 매핑
 */
fun Address.toDto(): AddressDto {
    return AddressDto(
        roadAddress = this.roadAddress.toDto(),
        legalAddress = this.legalAddress?.toDto(),
        adminAddress = this.adminAddress?.toDto(),
        coordinate = this.coordinate.toDto(),
    )
}

fun RoadAddress.toDto(): RoadAddressDto {
    return RoadAddressDto(
        fullAddress = this.fullAddress,
        zoneNo = this.zoneNo
    )
}

fun LegalAddress.toDto(): LegalAddressDto {
    return LegalAddressDto(
        fullAddress = this.fullAddress
    )
}

fun AdminAddress.toDto(): AdminAddressDto {
    return AdminAddressDto(
        fullAddress = this.fullAddress
    )
}

fun BusinessHour.toDto(): BusinessHourDto {
    return BusinessHourDto(
        dayOfWeek = this.dayOfWeek,
        openTime = this.openTime,
        closeTime = this.closeTime
    )
}

fun List<BusinessHour>.toDto(): List<BusinessHourDto> {
    return this.map { it.toDto() }
}

fun Coordinate.toDto(): CoordinateDto {
    return CoordinateDto(
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun PickUpInfo.toDto(): PickUpInfoDto {
    return PickUpInfoDto(
        pickupStartTime = this.pickupStartTime,
        pickupEndTime = this.pickupEndTime,
        pickupAvailableForTomorrow = this.pickupAvailableForTomorrow,
    )
}

fun ReviewInfo.toDto(): ReviewInfoDto {
    return ReviewInfoDto(
        ratingAverage = this.ratingAverage,
        ratingCount = this.ratingCount,
    )
}

fun StoreCategoryInfo.toDto(): StoreCategoryInfoDto {
    return StoreCategoryInfoDto(
        storeCategory = this.storeCategory.map { it.value },
        foodCategory = this.foodCategory?.map { it.value }
    )
}

fun Store.toDto(): StoreDto {
    return StoreDto(
        storeId = this.id,
        storeOwnerId = this.storeOwnerId,
        name = this.name.value,
        description = this.description,
        address = this.address.toDto(),
        businessNumber = this.businessNumber.value,
        contactNumber = this.contactNumber?.value,
        imageUrl = this.imageUrl?.value,
        businessHours = this.businessHours?.toDto()!!,
        storeCategoryInfo = this.storeCategoryInfo.toDto(),
        status = this.status,
        pickUpInfo = this.pickUpInfo.toDto(),
        reviewInfo = this.reviewInfo.toDto(),
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

/**
 * 상점 구독 domain을 DTO로 변환
 */
fun StoreSubscription.toDto(
    userName: String,
    storeName: String,
    mainImageUrl: String?,
    status: StoreEnum.StoreStatus,
    discountRate: Double,
    originalPrice: Int,
    discountedPrice: Int
): StoreSubscriptionDto {
    return StoreSubscriptionDto(
        id = this.id,
        userId = this.userId,
        userName = userName,
        storeId = this.storeId,
        storeName = storeName,
        mainImageUrl = mainImageUrl,
        status = status,
        discountRate = discountRate,
        originalPrice = originalPrice,
        discountedPrice = discountedPrice,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        deletedAt = this.deletedAt
    )
}