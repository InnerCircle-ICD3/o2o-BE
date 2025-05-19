package com.eatngo.store.dto

import com.eatngo.store.constant.StoreEnum
import com.eatngo.store.domain.*
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

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