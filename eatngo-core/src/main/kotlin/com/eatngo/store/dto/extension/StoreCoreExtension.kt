package com.eatngo.store.dto.extension

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.domain.Address
import com.eatngo.store.domain.AdminAddress
import com.eatngo.store.domain.LegalAddress
import com.eatngo.store.domain.Store
import com.eatngo.store.domain.StoreCategoryInfo
import com.eatngo.store.domain.StoreSubscription
import com.eatngo.store.dto.AddressDto
import com.eatngo.store.dto.AdminAddressDto
import com.eatngo.store.dto.BusinessHourDto
import com.eatngo.store.dto.LegalAddressDto
import com.eatngo.store.dto.PickUpInfoDto
import com.eatngo.store.dto.StoreCategoryInfoDto
import com.eatngo.store.dto.StoreDto
import com.eatngo.store.dto.StoreSubscriptionDto
import com.eatngo.store.vo.BusinessHourVO
import com.eatngo.store.vo.PickUpInfoVO

/**
 * core 모듈의 dto를 domain으로 매핑
 */

// LegalAddressDto → LegalAddress
fun LegalAddressDto.toDomain(): LegalAddress = LegalAddress(
    fullAddress = this.fullAddress
)

// AdminAddressDto → AdminAddress
fun AdminAddressDto.toDomain(): AdminAddress = AdminAddress(
    fullAddress = this.fullAddress
)

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
        description = this.description?.value,
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
 * VO -> DTO
 */
fun BusinessHourVO.toDto(): BusinessHourDto = BusinessHourDto(
    dayOfWeek = this.dayOfWeek,
    openTime = this.openTime,
    closeTime = this.closeTime
)

fun List<BusinessHourVO>?.toDto(): List<BusinessHourDto>? = this?.map { it.toDto() }

fun PickUpInfoVO.toDto(): PickUpInfoDto = PickUpInfoDto(
    pickupDay = this.pickupDay,
    pickupStartTime = this.pickupStartTime,
    pickupEndTime = this.pickupEndTime
)

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