package com.eatngo.search.dto

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.rdb.json_converter.BusinessHourJson
import java.time.LocalDateTime

data class SearchStoreRdbDto(
    val storeId: Long,
    val storeName: String,
    val storeImage: String, // 매장 이미지 S3 URL
    val storeCategory: List<String>,
    val foodCategory: List<String>, // 대표 판매 음식 종류
    val foodTypes: List<String>,
    val roadNameAddress: String,
    val latitude: Double,
    val longitude: Double,
    val storeStatus: StoreEnum.StoreStatus, // 매장 오픈 여부
    val businessHours: List<BusinessHourJson>,
    val updatedAt: LocalDateTime = LocalDateTime.now(), // 마지막 업데이트 시간
    val createdAt: LocalDateTime = LocalDateTime.now(), // 생성 시간
)
