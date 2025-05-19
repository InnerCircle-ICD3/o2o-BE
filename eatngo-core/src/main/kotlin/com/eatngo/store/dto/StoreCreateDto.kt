package com.eatngo.store.dto

import java.time.LocalTime

/**
 * 상점 생성 요청 DTO
 */
data class StoreCreateDto(
    val storeOwnerId: String,
    val name: String,
    val address: AddressDto,
    val businessNumber: String,
    val businessHours: List<BusinessHourDto>?,
    val contactNumber: String?,
    val description: String?,
    val imageUrl: String?,
    val pickupStartTime: LocalTime,
    val pickupEndTime: LocalTime,
    val pickupAvailableForTomorrow: Boolean,
    val storeCategory: List<String>,
    val foodCategory: List<String> = emptyList(),
)