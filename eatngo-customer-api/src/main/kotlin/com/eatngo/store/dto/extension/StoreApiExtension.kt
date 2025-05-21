package com.eatngo.store.dto.extension

import com.eatngo.store.dto.BusinessHourResponse
import com.eatngo.store.dto.StoreDetailResponse
import com.eatngo.store.dto.StoreDto
import com.eatngo.store.dto.StoreResponse
import com.eatngo.store.dto.StoreSubscriptionDto
import com.eatngo.store.dto.StoreSubscriptionResponse
import com.eatngo.store.dto.SubscriptionToggleResponse
import java.time.format.DateTimeFormatter

/**
 * dto -> response
 */

fun StoreDto.toStoreResponse(distance: Double? = null): StoreResponse {
    return StoreResponse(
        id = this.storeId,
        name = this.name,
        mainImageUrl = this.imageUrl,
        status = this.status.name, // enum → string
        pickupAvailableForTomorrow = this.pickUpInfo.pickupAvailableForTomorrow!!,
        distance = distance,
        foodCategory = this.storeCategoryInfo.foodCategory
    )
}

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
        pickupAvailableForTomorrow = this.pickUpInfo.pickupAvailableForTomorrow!!,
        status = this.status.name,
        ratingAverage = this.reviewInfo.ratingAverage,
        ratingCount = this.reviewInfo.ratingCount,
        foodCategory = this.storeCategoryInfo.foodCategory,
        storeCategory = this.storeCategoryInfo.storeCategory!!,
    )
}

fun StoreSubscriptionDto.toResponse(): StoreSubscriptionResponse {
    return StoreSubscriptionResponse(
        id = this.id,
        storeId = this.storeId,
        storeName = this.storeName,
        mainImageUrl = this.mainImageUrl,
        status = this.status.name,
        discountRate = this.discountRate,
        originalPrice = this.originalPrice,
        discountedPrice = this.discountedPrice,
        subscribedAt = this.createdAt
    )
}

fun StoreSubscriptionDto.toToggleResponse(): SubscriptionToggleResponse {
    return SubscriptionToggleResponse(
        id = this.id,
        userId = this.userId,
        storeId = this.storeId,
        subscribed = this.deletedAt == null,
        actionTime = this.updatedAt
    )
}