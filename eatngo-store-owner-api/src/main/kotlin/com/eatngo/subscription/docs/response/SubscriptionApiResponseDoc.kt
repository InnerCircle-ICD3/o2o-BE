package com.eatngo.subscription.docs.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "매장 구독자 간단 정보 래핑 응답 (점주용)")
data class SubscriptionApiResponseDoc(
    @Schema(description = "요청 성공 여부", example = "true")
    val success: Boolean = true,

    @Schema(description = "응답 데이터")
    val data: SubscriptionResponseDoc = SubscriptionResponseDoc()
)

@Schema(description = "매장 구독자 간단 정보 (점주용 응답)")
data class SubscriptionResponseDoc(
    @Schema(description = "구독 ID", example = "1001")
    val id: Long = 1001,

    @Schema(description = "매장 ID", example = "123")
    val storeId: Long = 123,

    @Schema(description = "구독한 사용자 계정 ID", example = "456")
    val userId: Long = 456,

    @Schema(description = "구독 여부(토글 결과)", example = "true")
    val subscribed: Boolean = true,

    @Schema(description = "구독/해제 시간", example = "2025-06-05T17:00:00")
    val actionTime: LocalDateTime = LocalDateTime.of(2025, 6, 5, 17, 0, 0)
)