package com.eatngo.store.dto

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.docs.request.StoreStatusUpdateRequestDocs
import com.eatngo.store.docs.request.StoreUpdateRequestDocs

/**
 * 매장 수정 요청용 dto
 */

data class StoreUpdateRequest(
    override val name: String? = null,

    // 주소 정보 (flat)
    override val roadNameAddress: String? = null,
    override val lotNumberAddress: String? = null,
    override val buildingName: String? = null,
    override val zipCode: String? = null,
    override val region1DepthName: String? = null,
    override val region2DepthName: String? = null,
    override val region3DepthName: String? = null,

    // 위치 정보
    override val latitude: Double? = null,
    override val longitude: Double? = null,

    // 운영 정보
    override val businessHours: List<BusinessHourDto>? = null,
    override val pickupDay: String? = null,

    // 부가 정보
    override val contact: String? = null,
    override val description: String? = null,
    override val mainImageUrl: String? = null,
    override val storeCategory: List<String>? = null,
    override val foodCategory: List<String>? = null
) : StoreUpdateRequestDocs {
    fun toStoreUpdateDto(storeOwnerId: Long): StoreUpdateDto {
        val addressDto = if (
            roadNameAddress != null && zipCode != null &&
            latitude != null && longitude != null
        ) {
            AddressDto(
                roadNameAddress = roadNameAddress,
                lotNumberAddress = lotNumberAddress,
                buildingName = buildingName,
                zipCode = zipCode,
                region1DepthName = region1DepthName,
                region2DepthName = region2DepthName,
                region3DepthName = region3DepthName,
                coordinate = CoordinateDto(
                    latitude = latitude,
                    longitude = longitude
                )
            )
        } else null

        // 카테고리 정보가 있는 경우에만 StoreCategoryInfoDto 생성
        val storeCategoryInfoDto = if (storeCategory != null || foodCategory != null) {
            StoreCategoryInfoDto(
                storeCategory = storeCategory,
                foodCategory = foodCategory
            )
        } else null

        return StoreUpdateDto(
            storeOwnerId = storeOwnerId,
            name = name,
            address = addressDto,
            businessHours = this.businessHours?.map { hour ->
                BusinessHourDto(
                    dayOfWeek = hour.dayOfWeek,
                    openTime = hour.openTime,
                    closeTime = hour.closeTime,
                )
            },
            pickUpDay = pickupDay,
            contactNumber = contact,
            description = description,
            mainImageUrl = mainImageUrl,
            storeCategoryInfo = storeCategoryInfoDto
        )
    }
}

data class StoreStatusUpdateRequest(override val status: StoreEnum.StoreStatus) : StoreStatusUpdateRequestDocs
