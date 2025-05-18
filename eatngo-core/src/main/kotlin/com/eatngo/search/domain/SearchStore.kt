package com.eatngo.search.domain

import com.eatngo.common.type.Point
import java.time.LocalDateTime

/**
 * SearchStore
 * 검색하는 가게의 정보를 담고 있는 클래스
 * 삭제 시 RealDelete로 삭제 -> 원본 가게 데이터가 PostgreSQL에 남아있음
 * TODO: MongoDB Document로 변환
 */
class SearchStore (
    var storeId: Long = 0L,
    var storeName: String = "",
    var storeImage: String = "", // 매장 이미지 S3 URL
    var storeCategory: List<String> = emptyList(),
    var foodCategory: List<String> = emptyList(),
    var roadAddress: String = "",
    var location: Point = Point(0.0, 0.0), // TODO: MongoDB GeoJSON으로 변환
    var updatedAt: LocalDateTime, // 마지막 업데이트 시간
    var createdAt: LocalDateTime, // 생성 시간
) {
    companion object {
        fun create(): SearchStore {
            return SearchStore(
                storeId = 0L,
                storeName = "",
                storeImage = "",
                storeCategory = emptyList(),
                foodCategory = emptyList(),
                roadAddress = "",
                location = Point(0.0, 0.0),
                updatedAt = LocalDateTime.now(),
                createdAt = LocalDateTime.now()
            )
        }
    }
}