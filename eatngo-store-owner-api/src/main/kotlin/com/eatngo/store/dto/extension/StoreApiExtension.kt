package com.eatngo.store.dto.extension

import com.eatngo.store.dto.AddressDto
import com.eatngo.store.dto.AdminAddressDto
import com.eatngo.store.dto.BusinessHourResponse
import com.eatngo.store.dto.CoordinateDto
import com.eatngo.store.dto.LegalAddressDto
import com.eatngo.store.dto.PickUpInfoDto
import com.eatngo.store.dto.ReviewInfoDto
import com.eatngo.store.dto.RoadAddressDto
import com.eatngo.store.dto.StoreCategoryInfoDto
import com.eatngo.store.dto.StoreCreateRequest
import com.eatngo.store.dto.StoreCreateDto
import com.eatngo.store.dto.StoreDetailResponse
import com.eatngo.store.dto.StoreDto
import com.eatngo.store.dto.StoreSubscriptionDto
import com.eatngo.store.dto.StoreUpdateDto
import com.eatngo.store.dto.StoreUpdateRequest
import com.eatngo.store.dto.SubscriptionResponseForStoreOwner
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * request -> dto
 */

fun StoreCreateRequest.toCoreDto(storeOwnerId: Long): StoreCreateDto {
    return StoreCreateDto(
        storeOwnerId = storeOwnerId,
        name = this.name,
        address = AddressDto(
            roadAddress = RoadAddressDto(
                fullAddress = this.roadFullAddress,
                zoneNo = this.roadZoneNo
            ),
            legalAddress = if (this.legalFullAddress != null && this.legalMainAddressNo != null) {
                LegalAddressDto(
                    fullAddress = this.legalFullAddress
                )
            } else null,
            adminAddress = this.adminFullAddress?.let { AdminAddressDto(it) },
            coordinate = CoordinateDto(
                longitude = this.longitude,
                latitude = this.latitude
            ),
        ),
        businessNumber = this.businessNumber,
        businessHours = this.businessHours,
        contactNumber = this.contact,
        description = this.description,
        imageUrl = this.mainImageUrl,
        pickUpInfo = PickUpInfoDto(
            pickupStartTime = this.pickupStartTime,
            pickupEndTime = this.pickupEndTime,
            pickupAvailableForTomorrow = this.pickupAvailableForTomorrow,
        ),
        storeCategoryInfo = StoreCategoryInfoDto(
            storeCategory = this.storeCategory,
            foodCategory = this.foodCategory,
        )
    )
}

fun StoreUpdateRequest.toCoreDto(): StoreUpdateDto {
    val roadAddress = RoadAddressDto(
        fullAddress = roadFullAddress!!,
        zoneNo = roadZoneNo!!
    )

    val legalAddress = if (legalFullAddress != null && legalMainAddressNo != null) {
        LegalAddressDto(
            fullAddress = legalFullAddress
        )
    } else null

    val adminAddress = adminFullAddress?.let { AdminAddressDto(it) }

    val coordinate = CoordinateDto(
        latitude = latitude!!,
        longitude = longitude!!,
    )

    val address = AddressDto(
        roadAddress = roadAddress,
        legalAddress = legalAddress,
        adminAddress = adminAddress,
        coordinate = coordinate
    )

    return StoreUpdateDto(
        name = name,
        address = address,
        businessHours = businessHours,
        contactNumber = contact,
        description = description,
        pickUpInfo = PickUpInfoDto(
            pickupStartTime = this.pickupStartTime,
            pickupEndTime = this.pickupEndTime,
            pickupAvailableForTomorrow = this.pickupAvailableForTomorrow ?: false,
        ),
        mainImageUrl = mainImageUrl,
        storeCategoryInfo = StoreCategoryInfoDto(
            storeCategory = this.storeCategory,
            foodCategory = this.foodCategory,
        )
    )
}


/**
 * dto -> response
 */

fun StoreDto.toDetailResponse(): StoreDetailResponse {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    return StoreDetailResponse(
        id = this.storeId,
        name = this.name,
        mainImageUrl = this.imageUrl,
        contact = this.contactNumber ?: "",
        description = this.description ?: "",
        businessNumber = this.businessNumber,
        businessHours = this.businessHours.map { hour ->
            BusinessHourResponse(
                dayOfWeek = hour.dayOfWeek.name,
                openTime = hour.openTime.format(timeFormatter),
                closeTime = hour.closeTime.format(timeFormatter),
            )
        },
        latitude = this.address.coordinate.latitude,
        longitude = this.address.coordinate.longitude,
        pickupStartTime = this.pickUpInfo.pickupStartTime?.format(timeFormatter)!!,
        pickupEndTime = this.pickUpInfo.pickupEndTime?.format(timeFormatter)!!,
        pickupAvailableForTomorrow = this.pickUpInfo.pickupAvailableForTomorrow,
        status = this.status.name,
        ratingAverage = this.reviewInfo.ratingAverage,
        ratingCount = this.reviewInfo.ratingCount,
        foodCategory = this.storeCategoryInfo.foodCategory,
        storeCategory = this.storeCategoryInfo.storeCategory,
    )
}

fun StoreSubscriptionDto.toResponse(): SubscriptionResponseForStoreOwner {
    return SubscriptionResponseForStoreOwner(
        id = this.id,
        storeId = this.storeId,
        userId = this.userId,
        userName = this.userName,
        subscribed = this.deletedAt == null,      // deletedAt이 null이면 구독중
        actionTime = this.updatedAt               // 마지막 변경 시각
    )
}