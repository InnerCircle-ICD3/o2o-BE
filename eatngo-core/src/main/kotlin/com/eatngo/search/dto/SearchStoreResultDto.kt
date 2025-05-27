package com.eatngo.search.dto

import com.eatngo.common.type.Coordinate
import com.eatngo.common.util.DistanceCalculator
import com.eatngo.search.constant.StoreEnum
import com.eatngo.search.domain.SearchStore
import java.time.LocalDateTime

data class SearchStoreResultDto(
    val storeList: List<SearchStoreDto>,
)

data class SearchStoreDto(
    val storeId: Long,
    val storeName: String,
    val storeImage: String = "", // 매장 이미지 URL
    val category: List<StoreEnum.StoreCategory>, // 대표 판매 음식 종류
    val distanceKm: Double, // 검색하는 유저와 매장 간의 거리(km)
    val open: Boolean, // 매장 오픈 여부
    val stock: Int, // 재고 수량
    val roadAddress: String, // 매장 주소(도로명 주소)
    val location: Coordinate, // 매장 위치(위도, 경도)
    val businessHours: BusinessHoursDto =
        BusinessHoursDto(
            openTime = LocalDateTime.now(),
            closeTime = LocalDateTime.now(),
        ),
    // 매장 영업 시간
    // TODO: 리뷰, 찜 기능
    val reviewCount: Int? = 0, // 리뷰 수
    val reviewScore: Double? = 5.0, // 리뷰 평점
    val isFavorite: Boolean? = false, // 찜 여부
) {
    companion object {
        fun from(
            userCoordinate: Coordinate,
            searchStore: SearchStore,
        ): SearchStoreDto =
            SearchStoreDto(
                storeId = searchStore.storeId,
                storeName = searchStore.storeName,
                storeImage = searchStore.storeImage,
                category = searchStore.category,
                roadAddress = searchStore.roadAddress,
                location = searchStore.location, // TODO: MongoDB GeoJSON -> Point 변환
                distanceKm =
                    DistanceCalculator.calculateDistance(
                        from = searchStore.location,
                        to = userCoordinate,
                    ),
                open = searchStore.open,
                businessHours = searchStore.businessHours,
                // TODO: 재고 수량은 Redis에서 가져와야 함(상품)
                stock = 0,
            )
    }
}

data class BusinessHoursDto(
    val openTime: LocalDateTime,
    val closeTime: LocalDateTime,
)
