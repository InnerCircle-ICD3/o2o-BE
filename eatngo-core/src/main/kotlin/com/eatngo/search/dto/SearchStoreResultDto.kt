package com.eatngo.search.dto

import com.eatngo.common.type.Point

data class SearchStoreResultDto (
    val storeList: List<SearchStore>,
)

data class SearchStore (
    val storeId: Long,
    val storeName: String,
    val storeImage: String="", // 매장 이미지 URL
    val storeCategory: List<String>,
    val foodCategory: List<String>, // 대표 판매 음식 종류
    val distanceKm: Double,         // 검색하는 유저와 매장 간의 거리(km)
    val isOpen: Boolean,            // 매장 오픈 여부
    val stock: Int,                 // 재고 수량
    val roadAddress: String,        // 매장 주소(도로명 주소)
    val location: Point,            // 매장 위치(위도, 경도)

    // TODO: 리뷰, 찜 기능
    val reviewCount: Int?=0,         // 리뷰 수
    val reviewScore: Double?=5.0,    // 리뷰 평점
    val isFavorite: Boolean?=false,  // 찜 여부
)
