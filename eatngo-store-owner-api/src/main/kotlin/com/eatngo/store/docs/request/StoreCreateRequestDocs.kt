package com.eatngo.store.docs.request

import com.eatngo.store.dto.BusinessHourDto
import io.swagger.v3.oas.annotations.media.Schema

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
        description = "사업자등록번호 (10자리 숫자를 문자열로 입력: 앞자리가 0인 경우 때문에 문자열로 입력해야 합니다.)",
        example = "1234567890",
        minLength = 10,
        maxLength = 10,
        required = true
    )
    val businessNumber: String

    @get:Schema(
        description = "도로명 주소 (공백 불가)",
        example = "서울특별시 강남구 테헤란로 123",
        required = true
    )
    val roadNameAddress: String?

    @get:Schema(
        description = "지번 주소 (공백 불가)",
        example = "서울특별시 강남구 역삼동 123-45",
        required = true
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
        maxLength = 5,
        required = true
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
        description = "위도(-90에서 90 사이)",
        example = "37.499590",
        required = true
    )
    val latitude: Double?

    @get:Schema(
        description = "경도(-180에서 180 사이)",
        example = "127.031722",
        required = true
    )
    val longitude: Double?

    @get:Schema(
        description = "픽업 가능한 날",
        example = "TODAY",
        allowableValues = ["TODAY", "TOMORROW"],
        required = true
    )
    val pickupDay: String?

    @get:Schema(
        description = "요일별 픽업 가능시간 목록",
        example = "[{\"dayOfWeek\":\"MONDAY\", \"openTime\":\"09:00:00\", \"closeTime\":\"18:00:00\"}]",
        allowableValues = ["dayOfWeek : MONDAY ~ SUNDAY", "openTime : HH:mm:ss", "closeTime : HH:mm:ss"]
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
        description = "매장 대표 이미지 URL(S3 presigned-url 이용)",
        example = "https://eatngo-app.s3.ap-northeast-2.amazonaws.com/store/otter.png"
    )
    val mainImageUrl: String?


    @get:Schema(
        description = "매장의 대분류 카테고리(리스트로 입력)",
        example = "[\"BREAD\"]",
        allowableValues = ["BREAD", "BAKERY", "CAFE", "DESSERT", "KOREAN", "FRUIT", "PIZZA", "SALAD", "RICECAKE"],
    )
    val storeCategory: List<String>?

    @get:Schema(
        description = "매장 정보에 보이는 음식 카테고리(리스트로 입력)",
        example = "[\"모닝빵\", \"밤식빵\", \"호두식빵\"]",
    )
    val foodCategory: List<String>?
}