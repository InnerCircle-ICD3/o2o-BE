package com.eatngo.store.docs

import com.eatngo.auth.annotaion.StoreOwnerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.store.dto.BusinessHourDto
import com.eatngo.store.dto.StoreCUDResponse
import com.eatngo.store.dto.StoreCreateRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.RequestBody

interface StoreCreateRequestDocs {

    @get:Schema(
        description = "매장명 (1~50자, 공백 불가)",
        example = "맛있는김밥집",
        minLength = 1,
        maxLength = 50,
        required = true
    )
    val name: String

    @get:Schema(
        description = "사업자등록번호 (10자리 숫자)",
        example = "1234567890",
        minLength = 10,
        maxLength = 10,
        required = true
    )
    val businessNumber: String

    @get:Schema(
        description = "도로명 주소 (공백 불가)",
        example = "서울특별시 강남구 테헤란로 123"
    )
    val roadNameAddress: String?

    @get:Schema(
        description = "지번 주소 (공백 불가)",
        example = "서울특별시 강남구 역삼동 123-45"
    )
    val lotNumberAddress: String?

    @get:Schema(
        description = "건물명",
        example = "삼성빌딩"
    )
    val buildingName: String?

    @get:Schema(
        description = "우편번호 (5자리 숫자)",
        example = "06222",
        minLength = 5,
        maxLength = 5
    )
    val zipCode: String?

    @get:Schema(
        description = "시/도",
        example = "서울특별시"
    )
    val region1DepthName: String?

    @get:Schema(
        description = "구/군",
        example = "강남구"
    )
    val region2DepthName: String?

    @get:Schema(
        description = "동/면/읍",
        example = "역삼동"
    )
    val region3DepthName: String?

    @get:Schema(
        description = "위도",
        example = "37.499590"
    )
    val latitude: Double?

    @get:Schema(
        description = "경도",
        example = "127.031722"
    )
    val longitude: Double?

    @get:Schema(
        description = "픽업 가능 요일 (MONDAY, TUESDAY ...)",
        example = "MONDAY"
    )
    val pickupDay: String?

    @get:Schema(
        description = "영업시간 목록",
        example = "[{\"dayOfWeek\":\"MONDAY\", \"openTime\":\"09:00:00\", \"closeTime\":\"18:00:00\"}]"
    )
    val businessHours: List<BusinessHourDto>?

    @get:Schema(
        description = "연락처 (예: 02-123-4567)",
        example = "02-123-4567"
    )
    val contact: String?

    @get:Schema(
        description = "매장 설명 (최대 500자)",
        example = "신선한 재료로 만든 김밥을 판매합니다.",
        maxLength = 500
    )
    val description: String?

    @get:Schema(
        description = "매장 대표 이미지 URL",
        example = "https://cdn.eatngo.com/store/1.jpg"
    )
    val mainImageUrl: String?

    @get:Schema(
        description = "매장 카테고리 (KOREAN, CHINESE 등)",
        example = "[\"KOREAN\"]"
    )
    val storeCategory: List<String>?

    @get:Schema(
        description = "음식 카테고리 (예: 분식, 한식 등)",
        example = "[\"분식\"]"
    )
    val foodCategory: List<String>?
}