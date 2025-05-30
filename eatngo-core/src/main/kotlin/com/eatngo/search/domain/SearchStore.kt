package com.eatngo.search.domain

import com.eatngo.common.constant.StoreEnum
import com.eatngo.common.type.CoordinateVO
import com.eatngo.search.dto.BusinessHoursDto
import java.time.LocalDateTime

/**
 * SearchStore
 * 검색하는 가게의 정보를 담고 있는 클래스
 * 삭제 시 RealDelete로 삭제 -> 원본 가게 데이터가 PostgreSQL에 남아있음
 */
class SearchStore(
    val storeId: Long = 0L,
    val storeName: String = "",
    val storeImage: String = "", // 매장 이미지 S3 URL
    val storeCategory: List<StoreEnum.StoreCategory> = emptyList(),
    val foodCategory: List<String> = emptyList(), // 대표 판매 음식 종류
    val roadAddress: String = "",
    val coordinate: CoordinateVO = CoordinateVO.from(0.0, 0.0),
    val open: Boolean = false, // 매장 오픈 여부
    val businessHours: BusinessHoursDto, // 매장 영업 시간
    val updatedAt: LocalDateTime, // 마지막 업데이트 시간
    val createdAt: LocalDateTime, // 생성 시간
) {
    companion object {
        fun create(): SearchStore =
            SearchStore(
                storeId = 0L,
                storeName = "",
                storeImage = "",
                storeCategory = emptyList(),
                foodCategory = emptyList(),
                roadAddress = "",
                coordinate = CoordinateVO.from(0.0, 0.0),
                open = true,
                businessHours =
                    BusinessHoursDto(
                        openTime = LocalDateTime.now(),
                        closeTime = LocalDateTime.now(),
                    ),
                updatedAt = LocalDateTime.now(),
                createdAt = LocalDateTime.now(),
            )
    }
}
