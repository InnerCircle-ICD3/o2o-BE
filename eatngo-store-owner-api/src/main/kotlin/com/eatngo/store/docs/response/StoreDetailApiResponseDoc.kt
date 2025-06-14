package com.eatngo.store.docs.response

import com.eatngo.docs.ApiResponseSuccessDoc
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalTime

/** 컨트롤러에서 반환되는 응답 래핑 */

@Schema(description = "매장 목록 응답 예시")
data class StoreDetailApiResponseDoc(
    @Schema(description = "요청 성공 여부", example = "true")
    override val success: Boolean = true,
    @Schema(description = "매장 목록 데이터")
    override val data: List<StoreDetailResponseDoc>
) : ApiResponseSuccessDoc<List<StoreDetailResponseDoc>>(success, data)

/** StoreDetailResponseDoc */

@Schema(description = "매장 상세 정보 응답")
data class StoreDetailResponseDoc(
    @Schema(description = "매장 ID", example = "123")
    val id: Long,

    @Schema(description = "매장명", example = "맛있는 분식집")
    val name: String,

    @Schema(description = "매장 대표 이미지 URL", example = "https://example.com/image.jpg")
    val mainImageUrl: String? = null,

    @Schema(description = "매장 연락처", example = "010-1234-5678")
    val contact: String,

    @Schema(description = "매장 설명", example = "분식 전문점입니다.")
    val description: String,

    @Schema(description = "사업자 등록번호", example = "1234567890")
    val businessNumber: String,

    @Schema(description = "영업 시간 목록")
    val businessHours: List<BusinessHourDoc>,

    @Schema(description = "매장 주소 정보")
    val address: AddressDoc,

    @Schema(description = "픽업가능일", example = "TODAY 또는 TOMORROW")
    val pickupDay: String? = null,

    @Schema(description = "오늘의 픽업 시작 시간", example = "10:00:00")
    val todayPickupStartTime: LocalTime? = null,

    @Schema(description = "오늘의 픽업 종료 시간", example = "20:00:00")
    val todayPickupEndTime: LocalTime? = null,

    @Schema(description = "매장 상태", example = "OPEN")
    val status: String,

    @Schema(description = "평균 평점", example = "4.5")
    val ratingAverage: Double,

    @Schema(description = "평점 개수", example = "123")
    val ratingCount: Int,

    @Schema(description = "음식 카테고리 목록", example = "[\"소금빵\", \"메론빵\"]")
    val foodCategory: List<String>? = null,

    @Schema(description = "매장 카테고리 목록", example = "[\"BREAD\"]")
    val storeCategory: List<String>? = null
)

/** DTO로 처리한 부분들 docs */
@Schema(description = "영업 시간 정보 예시")
data class BusinessHourDoc(
    @Schema(description = "요일", example = "MONDAY")
    val dayOfWeek: String,
    @Schema(description = "영업 시작 시간", example = "09:00:00")
    val openTime: String,
    @Schema(description = "영업 종료 시간", example = "18:00:00")
    val closeTime: String
)

@Schema(description = "주소 정보 예시")
data class AddressDoc(
    @Schema(description = "도로명 주소", example = "서울특별시 강남구 테헤란로 427")
    val roadNameAddress: String?,

    @Schema(description = "지번 주소", example = "서울특별시 강남구 삼성동 143-48")
    val lotNumberAddress: String?,

    @Schema(description = "건물명", example = "위워크타워")
    val buildingName: String?,

    @Schema(description = "우편번호", example = "06159")
    val zipCode: String?,

    @Schema(description = "지역 1단계명", example = "서울특별시")
    val region1DepthName: String?,

    @Schema(description = "지역 2단계명", example = "강남구")
    val region2DepthName: String?,

    @Schema(description = "지역 3단계명", example = "삼성동")
    val region3DepthName: String?,

    @Schema(description = "좌표 정보")
    val coordinate: CoordinateDoc
)

@Schema(description = "좌표 정보 예시")
data class CoordinateDoc(
    @Schema(description = "경도", example = "127.0276368")
    val longitude: Double,
    @Schema(description = "위도", example = "37.497942")
    val latitude: Double
)