package com.eatngo.store.dto


/**
 * 상점 수정 요청 DTO
 */
data class StoreUpdateDto(
    val storeOwnerId: Long,
    val name: String? = null,
    val address: AddressDto? = null,
    val businessHours: List<BusinessHourDto>? = null,
    val contactNumber: String? = null,
    val description: String? = null,
    val mainImageUrl: String? = null,
    val pickUpDay: String? = null,
    val storeCategoryInfo: StoreCategoryInfoDto? = null,
)