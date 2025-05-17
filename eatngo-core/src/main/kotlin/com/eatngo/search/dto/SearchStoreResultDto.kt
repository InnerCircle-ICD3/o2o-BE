package com.eatngo.search.dto

import com.eatngo.common.type.Point
import com.eatngo.common.util.DistanceCalculator
import com.eatngo.search.domain.SearchStore

data class SearchStoreResultDto (
    val storeList: List<SearchStoreDto>,
)

data class SearchStoreDto (
    val storeId: Long,
    val storeName: String,
    val storeImage: String="", // 매장 이미지 URL
    val storeCategory: List<String>,
    val foodCategory: List<String>, // 대표 판매 음식 종류
    val distanceKm: Double,         // 검색하는 유저와 매장 간의 거리(km)
    val open: Boolean,              // 매장 오픈 여부
    val stock: Int,                 // 재고 수량
    val roadAddress: String,        // 매장 주소(도로명 주소)
    val location: Point,            // 매장 위치(위도, 경도)

    // TODO: 리뷰, 찜 기능
    val reviewCount: Int?=0,         // 리뷰 수
    val reviewScore: Double?=5.0,    // 리뷰 평점
    val isFavorite: Boolean?=false,  // 찜 여부
) {
    companion object {
        fun from(userPoint: Point ,searchStore: SearchStore): SearchStoreDto {
            return SearchStoreDto(
                storeId = searchStore.storeId,
                storeName = searchStore.storeName,
                storeImage = searchStore.storeImage,
                storeCategory = searchStore.storeCategory,
                foodCategory = searchStore.foodCategory,
                roadAddress = searchStore.roadAddress,
                location = searchStore.location, // TODO: MongoDB GeoJSON -> Point 변환
                distanceKm = DistanceCalculator.calculateDistance(
                    from = searchStore.location,
                    to = userPoint,
                ),
                open = searchStore.open,
                // TODO: 재고 수량은 Redis에서 가져와야 함(상품)
                stock = 0,
                )
        }
    }
}
