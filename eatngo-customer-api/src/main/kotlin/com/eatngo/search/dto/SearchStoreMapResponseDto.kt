package com.eatngo.search.dto

// TODO: 공통 response 형태로 수정
data class SearchStoreMapResponseDto (
    val success: Boolean,
    val data: SearchStoreMapResultDto,
    val error: String? = null,
    val errorMessage: String? = null,
)