package com.eatngo.search.dto

import com.eatngo.common.constant.StoreEnum
import com.eatngo.common.type.CoordinateVO
import com.eatngo.common.util.DateTimeUtil
import com.eatngo.common.util.DistanceCalculator
import com.eatngo.search.domain.SearchStore
import com.eatngo.store.dto.BusinessHourDto
import com.eatngo.store.vo.BusinessHourVO
import java.time.LocalDateTime
import java.time.LocalTime

data class SearchStoreResultDto(
    val contents: List<SearchStoreDto>,
    val lastId: String,
) {
    companion object {
        fun from(
            userCoordinate: CoordinateVO,
            totalStockCountMap: Map<Long, Int>,
            searchStoreList: List<SearchStore>,
        ): SearchStoreResultDto {
            val contents =
                searchStoreList.map { searchStore ->
                    val distance =
                        DistanceCalculator.calculateDistance(
                            userCoordinate,
                            searchStore.coordinate.toVO(),
                        )
                    val stock = totalStockCountMap[searchStore.storeId] ?: -1
                    SearchStoreDto.from(searchStore, stock, distance)
                }

            return SearchStoreResultDto(
                contents = contents,
                lastId = searchStoreList.lastOrNull()?.paginationToken.orEmpty(),
            )
        }
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
    val roadNameAddress: String? = null, // 매장 주소(도로명 주소)
    val coordinate: CoordinateResultDto, // 매장 위치(위도, 경도)
    val businessHours: List<BusinessHourVO>, // 매장 영업 시간
    val todayPickupStartTime: LocalTime?, // 오늘 픽업 시작 시간
    val todayPickupEndTime: LocalTime?, // 오늘 픽업 종료 시간
    // TODO: 리뷰, 찜 기능
    val totalStockCount: Int = -1, // 전체 재고 수량 (-1: 오늘 등록 안함, 0: 재고 없음)
    val ratingAverage: Double, // 리뷰 평점
    val ratingCount: Int, // 리뷰 수
    val isFavorite: Boolean? = false, // 찜 여부
) {
    companion object {
        fun from(
            searchStore: SearchStore,
            stock: Int,
            distanceKm: Double,
        ): SearchStoreDto {
            // 픽업 가능 시간 TODO : 검색쪽 필터링 부분과 겹치는 로직으로 리팩토링 필요
            val now = LocalDateTime.now()
            val currentDayOfWeek = now.dayOfWeek
            val todayBusinessHours = searchStore.businessHours[currentDayOfWeek]

            val todayPickupStartTime =
                todayBusinessHours?.openTime?.let {
                    DateTimeUtil.parseHHmmToLocalTime(it)
                }
            val todayPickupEndTime =
                todayBusinessHours?.closeTime?.let {
                    DateTimeUtil.parseHHmmToLocalTime(it)
                }

            return SearchStoreDto(
                storeId = searchStore.storeId,
                storeName = searchStore.storeName,
                storeImage = searchStore.storeImage,
                storeCategory = searchStore.storeCategory,
                foodCategory = searchStore.foodCategory,
                roadNameAddress = searchStore.roadNameAddress,
                coordinate = searchStore.coordinate.toDto(),
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
                todayPickupStartTime = todayPickupStartTime,
                todayPickupEndTime = todayPickupEndTime,
                totalStockCount = stock,
                // TODO: 리뷰 관련 기능 구현 (Redis에서 가져오기?)
                ratingAverage = 3.0,
                ratingCount = 3,
            )
        }
    }
}
