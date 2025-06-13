package com.eatngo.search.dto

data class SearchStoreFoodTypeDto(
    val storeId: Long,
    val foodTypes: String, // JSON 형태로 저장된 음식 종류 목록
)
