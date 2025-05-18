package com.eatngo.search.domain

import com.eatngo.common.type.Point
import java.time.LocalDateTime
import java.time.ZonedDateTime

/**
 * SearchStore
 * 검색하는 가게의 정보를 담고 있는 클래스
 * 삭제 시 RealDelete로 삭제 -> 원본 가게 데이터가 PostgreSQL에 남아있음
 */
class SearchStore (
    var storeId: Long = 0L,
    var storeName: String = "",
    var storeImage: String = "", // 매장 이미지 S3 URL
    var storeCategory: List<String> = emptyList(),
    var foodCategory: List<String> = emptyList(),
    var roadAddress: String = "",
    var location: Point = Point(0.0, 0.0),
    var open: Boolean = false,  // 매장 오픈 여부
    var openTime: LocalDateTime, // 매장 오픈 시간
    var closeTime: LocalDateTime, // 매장 마감 시간
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
                open = true,
                openTime = LocalDateTime.now(),
                closeTime = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                createdAt = LocalDateTime.now()
            )
        }
    }
}