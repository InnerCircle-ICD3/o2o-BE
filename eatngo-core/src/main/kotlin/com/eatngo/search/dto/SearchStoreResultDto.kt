package com.eatngo.search.dto

data class SearchStoreResultDto (
    val storeList: List<SearchStore>,
)

data class SearchStore (
    val storeID: Long,
    val storeName: String,
    // TODO: 매장 카티고리, 음식 카테고리는 사양에 따라 변경될 수 있음
    val storeCategory: String,
    val foodCategory: List<String>, // 대표 판매 음식 종류
    val distanceKm: Double,         // 검색하는 유저와 매장 간의 거리(km)
    val openStatus: Int,            // 매장 오픈 상태 (0: 오늘 판매X, 1: 판매중, 2: 재고 소진)
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
