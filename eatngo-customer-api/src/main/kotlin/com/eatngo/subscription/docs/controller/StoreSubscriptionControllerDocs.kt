package com.eatngo.subscription.docs.controller

import com.eatngo.auth.annotation.CustomerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.docs.ApiResponseErrorDoc
import com.eatngo.subscription.docs.response.StoreSubscriptionListApiResponseDoc
import com.eatngo.subscription.docs.response.SubscriptionToggleApiResponseDoc
import com.eatngo.subscription.dto.StoreSubscriptionResponse
import com.eatngo.subscription.dto.SubscriptionToggleResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse  as SwaggerApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PathVariable

@Tag(name = "매장 구독", description = "매장 구독 관련 API")
interface StoreSubscriptionControllerDocs {

    @Operation(
        summary = "매장 구독/구독 취소 토글",
        description = """
        고객이 특정 매장을 구독하거나, 이미 구독 중인 경우 구독을 취소합니다.
        한 번의 요청으로 구독 상태를 토글할 수 있으며,
        이미 구독 중이라면 구독이 취소되고, 구독 중이 아니라면 구독이 등록됩니다.
    """
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "구독/구독 취소 토글 성공",
                content = [Content(schema = Schema(implementation = SubscriptionToggleApiResponseDoc::class))]
            ),
            SwaggerApiResponse(
                responseCode = "400",
                description = "잘못된 요청",
                content = [Content(schema = Schema(implementation = ApiResponseErrorDoc::class))]
            )
        ]
    )
    fun toggleSubscription(
        @Parameter(description = "구독할 매장 ID", required = true)
        @PathVariable storeId: Long,

        @Parameter(description = "구독하는 고객 ID", required = true)
        @CustomerId customerId: Long
    ): ApiResponse<SubscriptionToggleResponse>

    @Operation(
        summary = "구독 매장 목록 조회(내 구독 목록 조회)",
        description = "고객이 구독한 매장 목록을 조회합니다."
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "내 구독 매장 목록 조회 성공",
                content = [Content(schema = Schema(implementation = StoreSubscriptionListApiResponseDoc::class))]
            ),
            SwaggerApiResponse(
                responseCode = "400",
                description = "잘못된 요청",
                content = [Content(schema = Schema(implementation = ApiResponseErrorDoc::class))]
            )
        ]
    )
    fun getMySubscriptions(
        @Parameter(description = "조회하는 고객 ID", required = true)
        @CustomerId customerId: Long
    ): ApiResponse<List<StoreSubscriptionResponse>>
}