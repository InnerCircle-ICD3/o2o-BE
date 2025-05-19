package com.eatngo.store.dto

import com.eatngo.common.constant.StoreEnum
import java.time.LocalTime


/**
 * 상점 수정 요청 DTO
 */
data class StoreUpdateDto(
    val name: String,
    val address: AddressDto,
    val businessHours: List<BusinessHourDto>? = null,
    val contactNumber: String? = null,
    val description: String? = null,
    val mainImageUrl: String? = null,
    val pickUpInfo: PickUpInfoDto,
    val storeCategoryInfo: StoreCategoryInfoDto,
)