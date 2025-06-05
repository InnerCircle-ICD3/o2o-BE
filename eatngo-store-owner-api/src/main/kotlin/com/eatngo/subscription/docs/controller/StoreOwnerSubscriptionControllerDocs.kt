package com.eatngo.subscription.docs.controller

import com.eatngo.auth.annotaion.StoreOwnerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.docs.ApiResponseErrorDoc
import com.eatngo.subscription.docs.response.SubscriptionApiResponseDoc
import com.eatngo.subscription.dto.SubscriptionResponseForStoreOwner
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PathVariable

@Tag(name = "매장 구독 관리", description = "점주의 매장 구독자 관리 API")
interface StoreOwnerSubscriptionControllerDocs {

    @Operation(
        summary = "매장 구독자 목록 조회",
        description = "특정 매장의 구독자 목록을 조회합니다."
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "매장 구독자 목록 조회 성공",
                content = [Content(schema = Schema(implementation = SubscriptionApiResponseDoc::class))]
            ),
            SwaggerApiResponse(
                responseCode = "400",
                description = "잘못된 요청(400~500 에러 포함)",
                content = [Content(schema = Schema(implementation = ApiResponseErrorDoc::class))]
            )
        ]
    )
    fun getStoreSubscriptions(
        @Parameter(description = "조회할 매장 ID", required = true)
        @PathVariable storeId: Long,

        @Parameter(description = "점주 ID", required = true)
        @StoreOwnerId storeOwnerId: Long
    ): ApiResponse<List<SubscriptionResponseForStoreOwner>>
}