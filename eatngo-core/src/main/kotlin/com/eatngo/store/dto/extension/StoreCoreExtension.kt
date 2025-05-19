package com.eatngo.store.dto.extension

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.domain.Address
import com.eatngo.store.domain.AdminAddress
import com.eatngo.store.domain.BusinessHour
import com.eatngo.store.domain.LegalAddress
import com.eatngo.store.domain.RoadAddress
import com.eatngo.store.domain.Store
import com.eatngo.store.domain.StoreSubscription
import com.eatngo.store.dto.AddressDto
import com.eatngo.store.dto.AdminAddressDto
import com.eatngo.store.dto.BusinessHourDto
import com.eatngo.store.dto.LegalAddressDto
import com.eatngo.store.dto.RoadAddressDto
import com.eatngo.store.dto.StoreDto
import com.eatngo.store.dto.StoreSubscriptionDto

/**
 * core 모듈의 dto를 domain으로 매핑
 */
fun BusinessHourDto.toDomain(): BusinessHour = BusinessHour(
    dayOfWeek = this.dayOfWeek,
    openTime = this.openTime,
    closeTime = this.closeTime
)

// RoadAddressDto → RoadAddress
fun RoadAddressDto.toDomain(): RoadAddress = RoadAddress(
    fullAddress = this.fullAddress,
    zoneNo = this.zoneNo,
    buildingName = this.buildingName
)

// LegalAddressDto → LegalAddress
fun LegalAddressDto.toDomain(): LegalAddress = LegalAddress(
    fullAddress = this.fullAddress,
    mainAddressNo = this.mainAddressNo,
    subAddressNo = this.subAddressNo
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
        latitude = this.latitude,
        longitude = this.longitude
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
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun RoadAddress.toDto(): RoadAddressDto {
    return RoadAddressDto(
        fullAddress = this.fullAddress,
        zoneNo = this.zoneNo,
        buildingName = this.buildingName
    )
}

fun LegalAddress.toDto(): LegalAddressDto {
    return LegalAddressDto(
        fullAddress = this.fullAddress,
        mainAddressNo = this.mainAddressNo,
        subAddressNo = this.subAddressNo
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

fun Store.toDto(): StoreDto {
    return StoreDto(
        storeId = this.id,
        storeOwnerId = this.storeOwnerId,
        name = this.name,
        description = this.description,
        address = this.address.toDto(), // Address → AddressDto 변환
        businessNumber = this.businessNumber,
        contactNumber = this.contactNumber,
        imageUrl = this.imageUrl,
        businessHours = this.businessHours?.mapNotNull { it?.toDto() } ?: emptyList(),
        storeCategory = this.storeCategory,
        foodCategory = this.foodCategory?.filterNotNull() ?: emptyList(),
        status = this.status,
        pickupStartTime = this.pickupStartTime,
        pickupEndTime = this.pickupEndTime,
        pickupAvailableForTomorrow = this.pickupAvailableForTomorrow,
        ratingAverage = this.ratingAverage,
        ratingCount = this.ratingCount,
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

/**
 * 상점 구독 DTO를 domain으로 변환
 */
