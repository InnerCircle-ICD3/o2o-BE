package com.eatngo.search.dto

import com.eatngo.common.type.CoordinateVO
import com.eatngo.common.util.DistanceCalculator
import com.eatngo.search.constant.StoreEnum
import com.eatngo.search.domain.SearchStore
import java.time.LocalDateTime

data class SearchStoreResultDto(
    val storeList: List<SearchStoreDto>,
) {
    companion object {
        fun from(
            userCoordinate: CoordinateVO,
            searchStoreList: List<SearchStore>,
        ): SearchStoreResultDto =
            SearchStoreResultDto(
                storeList =
                    searchStoreList.map { searchStore ->
                        SearchStoreDto.from(userCoordinate, searchStore)
                    },
            )
    }
}

data class SearchStoreDto(
    val storeId: Long,
    val storeName: String,
    val storeImage: String = "", // 매장 이미지 URL
    val storeCategory: List<StoreEnum.StoreCategory>, // 대표 판매 음식 종류
    val foodCategory: List<String>, // 음식 종류
    val distanceKm: Double, // 검색하는 유저와 매장 간의 거리(km)
    val open: Boolean, // 매장 오픈 여부
    val stock: Int, // 재고 수량
    val roadAddress: String, // 매장 주소(도로명 주소)
    val coordinate: CoordinateVO, // 매장 위치(위도, 경도)
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
            userCoordinate: CoordinateVO,
            searchStore: SearchStore,
        ): SearchStoreDto =
            SearchStoreDto(
                storeId = searchStore.storeId,
                storeName = searchStore.storeName,
                storeImage = searchStore.storeImage,
                storeCategory = searchStore.storeCategory,
                foodCategory = searchStore.foodCategory,
                roadAddress = searchStore.roadAddress,
                coordinate = searchStore.coordinate,
                distanceKm =
                    DistanceCalculator.calculateDistance(
                        from = searchStore.coordinate,
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
