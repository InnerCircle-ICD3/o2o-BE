package com.eatngo.subscription.docs.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/** 컨트롤러에서 반환되는 응답 래핑 */
@Schema(description = "내 구독 매장 목록 응답 래퍼")
data class StoreSubscriptionListApiResponseDoc(
    @Schema(description = "요청 성공 여부", example = "true")
    val success: Boolean = true,
    @Schema(description = "응답 데이터")
    val data: List<StoreSubscriptionResponseDoc> = listOf(StoreSubscriptionResponseDoc())
)

/** StoreSubscriptionResponseDoc */
@Schema(description = "내 구독 매장 요약 정보 예시")
data class StoreSubscriptionResponseDoc(
    @Schema(description = "구독 ID", example = "1001")
    val id: Long = 1001,

    @Schema(description = "매장 ID", example = "123")
    val storeId: Long = 123,

    @Schema(description = "매장명", example = "맛있는 분식집")
    val storeName: String = "맛있는 분식집",

    @Schema(description = "매장 대표 이미지", example = "https://example.com/image.jpg")
    val mainImageUrl: String? = "https://example.com/image.jpg",

    @Schema(description = "매장 상태", example = "OPEN")
    val status: String = "OPEN",

    @Schema(description = "할인율", example = "0.1")
    val discountRate: Double? = 0.1,

    @Schema(description = "원가", example = "12000")
    val originalPrice: Int? = 12000,

    @Schema(description = "할인된 가격", example = "10800")
    val discountedPrice: Int? = 10800,

    @Schema(description = "구독 일시", example = "2025-06-05T17:00:00")
    val subscribedAt: LocalDateTime = LocalDateTime.of(2025, 6, 5, 17, 0, 0)
)