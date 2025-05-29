package com.eatngo.store.dto

/**
 * 상점 생성 요청 DTO
 */
data class StoreCreateDto(
    val storeOwnerId: Long,
    val name: String,
    val address: AddressDto,
    val businessNumber: String,
    val businessHours: List<BusinessHourDto>?,
    val contactNumber: String?,
    val description: String?,
    val imageUrl: String?,
    val pickUpInfo: PickUpInfoDto,
    val storeCategoryInfo: StoreCategoryInfoDto
)