package com.eatngo.search.dto

import com.eatngo.common.constant.StoreEnum
import com.eatngo.common.type.CoordinateVO
import com.eatngo.common.util.DateTimeUtil
import com.eatngo.common.util.DistanceCalculator
import com.eatngo.search.domain.SearchStore
import com.eatngo.store.dto.BusinessHourDto
import com.eatngo.store.vo.BusinessHourVO
import com.eatngo.store.vo.PickUpDayVO
import java.time.LocalTime

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
                        SearchStoreDto.from(
                            searchStore,
                            DistanceCalculator.calculateDistance(
                                from = searchStore.coordinate.toVO(),
                                to = userCoordinate,
                            ),
                        )
                    },
            )

        fun from(searchStoreList: List<SearchStoreWithDistance>): SearchStoreResultDto =
            SearchStoreResultDto(
                storeList =
                    searchStoreList.map { searchStore ->
                        SearchStoreDto.from(searchStore.store, searchStore.distance)
                    },
            )
    }
}

data class SearchStoreDto(
    val storeId: Long,
    val storeName: String,
    val storeImage: String, // 매장 이미지 URL
    val storeCategory: List<StoreEnum.StoreCategory>, // 대표 판매 음식 종류
    val foodCategory: List<String>, // 음식 종류
    val distanceKm: Double, // 검색하는 유저와 매장 간의 거리(km)
    val status: StoreEnum.StoreStatus, // 매장 오픈 여부
    val stock: Int, // 재고 수량
    val roadNameAddress: String, // 매장 주소(도로명 주소)
    val coordinate: CoordinateVO, // 매장 위치(위도, 경도)
    val businessHours: List<BusinessHourVO>, // 매장 영업 시간
    val pickUpDay: PickUpDayVO,
    val pickupHour: PickupHour, // 매장 픽업 가능 시간
    // TODO: 리뷰, 찜 기능
    val reviewCount: Int? = 0, // 리뷰 수
    val reviewScore: Double? = 5.0, // 리뷰 평점
    val isFavorite: Boolean? = false, // 찜 여부
) {
    companion object {
        fun from(
            searchStore: SearchStore,
            distanceKm: Double,
        ): SearchStoreDto =
            SearchStoreDto(
                storeId = searchStore.storeId,
                storeName = searchStore.storeName,
                storeImage = searchStore.storeImage,
                storeCategory = searchStore.storeCategory,
                foodCategory = searchStore.foodCategory,
                roadNameAddress = searchStore.roadNameAddress,
                coordinate = searchStore.coordinate.toVO(),
                distanceKm = distanceKm,
                status = searchStore.status.toStoreStatus(),
                businessHours =
                    BusinessHourVO.fromList(
                        searchStore.businessHours.map { (dayOfWeek, timeRange) ->
                            BusinessHourDto(
                                dayOfWeek = dayOfWeek,
                                openTime = DateTimeUtil.parseHHmmToLocalTime(timeRange.openTime),
                                closeTime = DateTimeUtil.parseHHmmToLocalTime(timeRange.closeTime),
                            )
                        },
                    ),
                // TODO: 픽업 가능 요일 선택 MongoDB 에도 추가
                pickUpDay = PickUpDayVO.from(searchStore.pickUpDay.name),
                pickupHour =
                    PickupHour(
                        startTime = DateTimeUtil.parseHHmmToLocalTime(searchStore.pickupHour.openTime),
                        endTime = DateTimeUtil.parseHHmmToLocalTime(searchStore.pickupHour.closeTime),
                    ),
                // TODO: 재고 수량은 Redis에서 가져와야 함(상품)
                stock = 0,
            )
    }
}

data class PickupHour(
    val startTime: LocalTime,
    val endTime: LocalTime
)