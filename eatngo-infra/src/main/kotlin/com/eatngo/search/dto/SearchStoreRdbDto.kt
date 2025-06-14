package com.eatngo.search.dto

import com.eatngo.common.constant.StoreEnum
import com.eatngo.search.domain.Coordinate
import com.eatngo.search.domain.SearchStore
import com.eatngo.search.domain.SearchStoreStatus
import com.eatngo.search.domain.TimeRange
import com.eatngo.store.rdb.json_converter.BusinessHourJson
import com.eatngo.store.rdb.json_converter.FoodCategoryJson
import com.eatngo.store.rdb.json_converter.StoreCategoryJson
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime

data class SearchStoreRdbDto(
    val storeId: Long,
    val storeName: String,
    val storeImage: String?, // 매장 이미지 S3 URL
    val storeCategory: StoreCategoryJson,
    val foodCategory: FoodCategoryJson, // 대표 판매 음식 종류
    val foodTypes: String?, // 매장에서 판매하는 음식 종류 -> flat한 문자열로 저장
    val roadNameAddress: String,
    val latitude: Double,
    val longitude: Double,
    val storeStatus: StoreEnum.StoreStatus, // 매장 오픈 여부
    val businessHours: List<BusinessHourJson>,
    val updatedAt: LocalDateTime = LocalDateTime.now(), // 마지막 업데이트 시간
    val createdAt: LocalDateTime = LocalDateTime.now(), // 생성 시간
) {
    fun toSearchStore(): SearchStore =
        SearchStore(
            storeId = storeId,
            storeName = storeName,
            storeImage = storeImage ?: "",
            storeCategory = storeCategory.value,
            foodCategory = foodCategory.value,
            foodTypes = foodTypes?.split(",")?.map { it.trim() } ?: emptyList(),
            roadNameAddress = roadNameAddress,
            coordinate =
                Coordinate.from(
                    latitude = latitude,
                    longitude = longitude,
                ),
            status = SearchStoreStatus.from(storeStatus),
            businessHours =
                businessHours.associate {
                    DayOfWeek.valueOf(it.dayOfWeek) to
                        TimeRange.from(LocalTime.parse(it.openTime), LocalTime.parse(it.closeTime))
                },
            updatedAt = updatedAt,
            createdAt = createdAt,
        )
}
