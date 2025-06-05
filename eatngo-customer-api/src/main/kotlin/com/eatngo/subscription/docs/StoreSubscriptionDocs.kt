package com.eatngo.subscription.docs

import com.eatngo.auth.annotation.CustomerId
import com.eatngo.common.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PathVariable

@Tag(name = "매장 구독", description = "매장 구독 관련 API")
interface StoreSubscriptionDocs {

    @Operation(
        summary = "매장 구독하기",
        description = "고객이 특정 매장을 구독합니다. 구독하면 매장의 새로운 상품이나 이벤트 알림을 받을 수 있습니다."
    )
    fun subscribeStore(
        @Parameter(description = "구독할 매장 ID", required = true)
        @PathVariable storeId: Long,
        
        @Parameter(description = "구독하는 고객 ID", required = true)
        @CustomerId customerId: Long
    ): ApiResponse<StoreSubscriptionResponse>

    @Operation(
        summary = "매장 구독 취소",
        description = "고객이 구독한 매장의 구독을 취소합니다."
    )
    fun unsubscribeStore(
        @Parameter(description = "구독 취소할 매장 ID", required = true)
        @PathVariable storeId: Long,
        
        @Parameter(description = "구독 취소하는 고객 ID", required = true)
        @CustomerId customerId: Long
    ): ApiResponse<StoreSubscriptionResponse>

    @Operation(
        summary = "구독 매장 목록 조회",
        description = "고객이 구독한 매장 목록을 조회합니다."
    )
    fun getSubscribedStores(
        @Parameter(description = "조회하는 고객 ID", required = true)
        @CustomerId customerId: Long
    ): ApiResponse<List<StoreSubscriptionResponse>>
}

@Schema(description = "매장 구독 응답")
data class StoreSubscriptionResponse(
    @field:Schema(description = "구독 ID", example = "1")
    val subscriptionId: Long,

    @field:Schema(description = "매장 ID", example = "1")
    val storeId: Long,

    @field:Schema(description = "매장명", example = "맛있는김밥집")
    val storeName: String,

    @field:Schema(description = "매장 이미지 URL", example = "https://cdn.eatngo.com/store/1.jpg")
    val storeImageUrl: String?,

    @field:Schema(description = "구독 상태", example = "SUBSCRIBED", allowableValues = ["SUBSCRIBED", "UNSUBSCRIBED"])
    val status: String
) 