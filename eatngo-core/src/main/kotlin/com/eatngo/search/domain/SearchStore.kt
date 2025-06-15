package com.eatngo.search.domain

import com.eatngo.common.constant.StoreEnum
import java.math.BigDecimal
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
    var foodTypes: List<String>?, // 대표 판매 음식 종류(검색용)
    val roadNameAddress: String? = null, // 매장 주소(도로명주소)
    val coordinate: Coordinate, // 매장 위치(위도, 경도)
    val status: SearchStoreStatus, // 매장 오픈 여부
    val businessHours: Map<DayOfWeek, TimeRange>, // 매장 영업 시간
    var totalReviewCount: Int? = 0, // 총 리뷰 수
    var averageRating: BigDecimal? = BigDecimal.ZERO, // 평균 평점
    val paginationToken: String? = null, // 검색 paginationToken
    val updatedAt: LocalDateTime, // 마지막 업데이트 시간
    val createdAt: LocalDateTime, // 생성 시간
    val deletedAt: LocalDateTime? = null, // 삭제 시간 (null: 삭제 안됨, not null: 삭제됨) -> RDB에서만 사용
)
