package com.eatngo.subscription.docs

import com.eatngo.auth.annotaion.StoreOwnerId
import com.eatngo.common.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PathVariable

@Tag(name = "매장 구독 관리", description = "점주의 매장 구독자 관리 API")
interface StoreOwnerSubscriptionDocs {

    @Operation(
        summary = "매장 구독자 목록 조회",
        description = "특정 매장의 구독자 목록을 조회합니다."
    )
    fun getStoreSubscribers(
        @Parameter(description = "조회할 매장 ID", required = true)
        @PathVariable storeId: Long,
        
        @Parameter(description = "점주 ID", required = true)
        @StoreOwnerId storeOwnerId: Long
    ): ApiResponse<List<StoreSubscriberResponse>>

    @Operation(
        summary = "매장 구독자 통계 조회",
        description = "특정 매장의 구독자 통계 정보를 조회합니다."
    )
    fun getStoreSubscriptionStats(
        @Parameter(description = "조회할 매장 ID", required = true)
        @PathVariable storeId: Long,
        
        @Parameter(description = "점주 ID", required = true)
        @StoreOwnerId storeOwnerId: Long
    ): ApiResponse<StoreSubscriptionStats>
}

@Schema(description = "매장 구독자 정보")
data class StoreSubscriberResponse(
    @field:Schema(description = "구독자 ID", example = "1")
    val subscriberId: Long,

    @field:Schema(description = "구독자 닉네임", example = "맛있는거좋아")
    val nickname: String,

    @field:Schema(description = "구독 시작일", example = "2024-03-15T14:30:00")
    val subscribedAt: String,

    @field:Schema(description = "최근 주문일", example = "2024-03-20T12:00:00")
    val lastOrderAt: String?
)

@Schema(description = "매장 구독 통계")
data class StoreSubscriptionStats(
    @field:Schema(description = "전체 구독자 수", example = "150")
    val totalSubscribers: Int,

    @field:Schema(description = "이번 달 신규 구독자 수", example = "25")
    val newSubscribersThisMonth: Int,

    @field:Schema(description = "이번 달 구독 취소자 수", example = "5")
    val unsubscribersThisMonth: Int,

    @field:Schema(description = "구독자 평균 주문 횟수", example = "3.5")
    val averageOrdersPerSubscriber: Double
) 