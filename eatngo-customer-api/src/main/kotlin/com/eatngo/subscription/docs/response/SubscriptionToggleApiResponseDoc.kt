package com.eatngo.subscription.docs.response

import com.eatngo.docs.ApiResponseSuccessDoc
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/** 컨트롤러에서 반환되는 응답 래핑 */
@Schema(description = "매장 구독/구독 취소 토글 응답 예시")
data class SubscriptionToggleApiResponseDoc(
    @Schema(description = "요청 성공 여부", example = "true")
    override val success: Boolean = true,
    @Schema(description = "응답 데이터")
    override val data: SubscriptionToggleResponseDoc
) : ApiResponseSuccessDoc<SubscriptionToggleResponseDoc>(success, data)

/** SubscriptionToggleResponseDoc */
@Schema(description = "매장 구독/구독 취소 토글 응답 예시")
data class SubscriptionToggleResponseDoc(
    @Schema(description = "구독 ID", example = "1001")
    val id: Long,

    @Schema(description = "구독한 사용자 계정 ID", example = "456")
    val userId: Long,

    @Schema(description = "매장 ID", example = "123")
    val storeId: Long,

    @Schema(description = "구독 여부", example = "true")
    val subscribed: Boolean,

    @Schema(description = "구독/해제 시간", example = "2025-06-05T17:00:00")
    val actionTime: LocalDateTime
)