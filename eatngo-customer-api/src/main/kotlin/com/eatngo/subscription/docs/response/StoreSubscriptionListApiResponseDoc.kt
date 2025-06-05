package com.eatngo.subscription.docs.response

import com.eatngo.docs.ApiResponseSuccessDoc
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/** 컨트롤러에서 반환되는 응답 래핑 */
@Schema(description = "내 구독 매장 목록 응답")
data class StoreSubscriptionListApiResponseDoc(
    @Schema(description = "요청 성공 여부", example = "true")
    override val success: Boolean = true,
    @Schema(description = "응답 데이터")
    override val data: List<StoreSubscriptionResponseDoc>
) : ApiResponseSuccessDoc<List<StoreSubscriptionResponseDoc>>(success, data)

/** StoreSubscriptionResponseDoc */
@Schema(description = "내 구독 매장 요약 정보 예시")
data class StoreSubscriptionResponseDoc(
    @Schema(description = "구독 ID", example = "1001")
    val id: Long,

    @Schema(description = "매장 ID", example = "123")
    val storeId: Long,

    @Schema(description = "매장명", example = "맛있는 분식집")
    val storeName: String,

    @Schema(description = "매장 대표 이미지", example = "https://example.com/image.jpg")
    val mainImageUrl: String?,

    @Schema(description = "매장 상태", example = "OPEN")
    val status: String,

    @Schema(description = "할인율", example = "0.1")
    val discountRate: Double,

    @Schema(description = "원가", example = "12000")
    val originalPrice: Int,

    @Schema(description = "할인된 가격", example = "10800")
    val discountedPrice: Int,

    @Schema(description = "구독 일시", example = "2025-06-05T17:00:00")
    val subscribedAt: LocalDateTime
)