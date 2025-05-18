package com.eatngo.store.dto.extension

import com.eatngo.store.dto.AddressDto
import com.eatngo.store.dto.AdminAddressDto
import com.eatngo.store.dto.LegalAddressDto
import com.eatngo.store.dto.RoadAddressDto
import com.eatngo.store.dto.StoreCreateRequest
import com.eatngo.store.dto.StoreCreateDto
import com.eatngo.store.dto.StoreDetailResponse
import com.eatngo.store.dto.StoreDto
import com.eatngo.store.dto.StoreSubscriptionDto
import com.eatngo.store.dto.StoreUpdateDto
import com.eatngo.store.dto.StoreUpdateRequest
import com.eatngo.store.dto.SubscriptionResponseForStoreOwner
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * request -> dto
 */

fun StoreCreateRequest.toCoreDto(storeOwnerId: String): StoreCreateDto {
    return StoreCreateDto(
        storeOwnerId = storeOwnerId,
        name = this.name,
        address = AddressDto(
            roadAddress = RoadAddressDto(
                fullAddress = this.roadFullAddress,
                zoneNo = this.roadZoneNo,
                buildingName = this.roadBuildingName
            ),
            legalAddress = if (this.legalFullAddress != null && this.legalMainAddressNo != null) {
                LegalAddressDto(
                    fullAddress = this.legalFullAddress,
                    mainAddressNo = this.legalMainAddressNo,
                    subAddressNo = this.legalSubAddressNo
                )
            } else null,
            adminAddress = this.adminFullAddress?.let { AdminAddressDto(it) },
            latitude = this.latitude,
            longitude = this.longitude
        ),
        businessNumber = this.businessNumber,
        businessHours = this.businessHours,
        contactNumber = this.contact,
        description = this.description,
        imageUrl = this.mainImageUrl,
        pickupStartTime = LocalTime.parse(this.pickupStartTime),
        pickupEndTime = LocalTime.parse(this.pickupEndTime),
        pickupAvailableForTomorrow = this.pickupAvailableForTomorrow,
        categories = this.categories
    )
}

fun StoreUpdateRequest.toCoreDto(): StoreUpdateDto {
    val roadAddress = RoadAddressDto(
        fullAddress = roadFullAddress!!,
        zoneNo = roadZoneNo!!,
        buildingName = roadBuildingName
    )

    val legalAddress = if (legalFullAddress != null && legalMainAddressNo != null) {
        LegalAddressDto(
            fullAddress = legalFullAddress,
            mainAddressNo = legalMainAddressNo,
            subAddressNo = legalSubAddressNo
        )
    } else null

    val adminAddress = adminFullAddress?.let { AdminAddressDto(it) }

    val address = AddressDto(
        roadAddress = roadAddress,
        legalAddress = legalAddress,
        adminAddress = adminAddress,
        latitude = latitude!!,
        longitude = longitude!!
    )

    return StoreUpdateDto(
        name = name,
        address = address,
        businessNumber = businessNumber,
        businessHours = businessHours,
        contactNumber = contact,
        description = description,
        pickupStartTime = pickupStartTime?.let { LocalTime.parse(it) },
        pickupEndTime = pickupEndTime?.let { LocalTime.parse(it) },
        pickupAvailableForTomorrow = pickupAvailableForTomorrow,
        mainImageUrl = mainImageUrl,
        categories = categories
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
        businessHour = this.businessHours.map {
            "${it.dayOfWeek.name} ${it.openTime.format(timeFormatter)}~${it.closeTime.format(timeFormatter)}"
        },
        latitude = this.address.latitude,
        longitude = this.address.longitude,
        pickupStartTime = this.pickupStartTime.format(timeFormatter),
        pickupEndTime = this.pickupEndTime.format(timeFormatter),
        pickupAvailableForTomorrow = this.pickupAvailableForTomorrow,
        status = this.status.name,
        ratingAverage = this.ratingAverage,
        ratingCount = this.ratingCount,
        categories = this.categories
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