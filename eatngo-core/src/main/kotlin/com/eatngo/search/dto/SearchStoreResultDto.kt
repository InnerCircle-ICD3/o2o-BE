package com.eatngo.search.dto

data class SearchStoreResultDto (
    val storeList: List<SearchStore>,
)

data class SearchStore (
    val storeID: Long,
    val storeName: String,
    val storeCategory: List<String>,
    val foodCategory: List<String>, // 대표 판매 음식 종류
    val distanceKm: Double,         // 검색하는 유저와 매장 간의 거리(km)
    val stock: Int,                 // 재고 수량
    val address: Address,           // 매장 주소
    // TODO: 리뷰, 찜 기능
    val reviewCount: Int=0,         // 리뷰 수
    val reviewScore: Double=5.0,    // 리뷰 평점
    val isFavorite: Boolean=false,  // 찜 여부
)

data class Address (
    val address: String, // 도로명주소
    val point: Point,    // 위경도
)
