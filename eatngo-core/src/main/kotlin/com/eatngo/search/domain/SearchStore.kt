package com.eatngo.search.domain

import com.eatngo.common.constant.StoreEnum
import java.time.DayOfWeek
import java.time.LocalDateTime

/**
 * SearchStore
 * 검색하는 가게의 정보를 담고 있는 클래스
 * 도메인 결합도를 최소화하기 위해 SearchStore는 검색 결과에 필요한 최소한의 정보만 포함
 * 삭제 시 RealDelete로 삭제 -> 원본 가게 데이터가 PostgreSQL에 남아있음
 */
class SearchStore(
    val storeId: Long,
    val storeName: String,
    val storeImage: String, // 매장 이미지 S3 URL
    val storeCategory: List<StoreEnum.StoreCategory>, // 매장 카테고리
    val foodCategory: List<String>, // 대표 판매 음식 종류
    val roadNameAddress: String, // 매장 주소(도로명주소)
    val coordinate: Coordinate, // 매장 위치(위도, 경도)
    val status: SearchStoreStatus, // 매장 오픈 여부
    val businessHours: Map<DayOfWeek, TimeRange>, // 매장 영업 시간
    val pickUpDay: StoreEnum.PickupDay, //픽업가능 요일
    val pickupHour: TimeRange, // 매장 픽업 가능 시간
    val updatedAt: LocalDateTime, // 마지막 업데이트 시간
    val createdAt: LocalDateTime, // 생성 시간
) {
    companion object {
        /**
         * 임시 Mock 데이터 생성용 생성자
         * TODO: 삭제 예정
         */
        fun getMockSearchStoreList(size: Int): List<SearchStore> =
            (1..size).map { idx ->
                SearchStore(
                    storeId = idx.toLong(),
                    storeName = "Mock Store $idx",
                    coordinate = Coordinate.from(37.001000 + idx * 0.00001, 127.001000 + idx * 0.00001),
                    storeCategory = listOf(enumValues<StoreEnum.StoreCategory>().random()),
                    foodCategory = listOf("호밀빵", "케이크"),
                    roadNameAddress = "서울시 강남구 테헤란로 123",
                    storeImage = "https://eatngo-app.s3.ap-northeast-2.amazonaws.com/store/pu.png",
                    status = SearchStoreStatus.OPEN,
                    businessHours =
                        mapOf(
                            DayOfWeek.MONDAY to TimeRange("08:00", "20:00"),
                            DayOfWeek.TUESDAY to TimeRange("08:00", "20:00"),
                            DayOfWeek.WEDNESDAY to TimeRange("08:00", "20:00"),
                            DayOfWeek.THURSDAY to TimeRange("08:00", "20:00"),
                            DayOfWeek.FRIDAY to TimeRange("08:00", "20:00"),
                            DayOfWeek.SATURDAY to TimeRange("11:00", "14:00"),
                            DayOfWeek.SUNDAY to TimeRange("10:00", "14:00"),
                        ),
                    pickUpDay = StoreEnum.PickupDay.TODAY,
                    pickupHour = TimeRange("08:00", "20:00"),
                    updatedAt = LocalDateTime.now(),
                    createdAt = LocalDateTime.now(),
                )
            }
    }
}
