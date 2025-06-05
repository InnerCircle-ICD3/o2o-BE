package com.eatngo.store.docs.controller

import com.eatngo.docs.ApiResponseErrorDoc
import com.eatngo.store.docs.response.StoreDetailApiResponseDoc
import com.eatngo.store.dto.StoreDetailResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PathVariable

@Tag(name = "매장 API", description = "매장 관련 API")
interface StoreCustomerControllerDocs {

    @Operation(
        summary = "매장 상세 조회",
        description = "매장의 상세 정보를 조회합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "매장 조회 성공",
                content = [Content(schema = Schema(implementation = StoreDetailApiResponseDoc::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청(400~500 에러 포함)",
                content = [Content(schema = Schema(implementation = ApiResponseErrorDoc::class))]
            )
        ]
    )
    fun getStoreById(
        @Parameter(description = "조회할 매장 ID", required = true)
        @PathVariable storeId: Long
    ): com.eatngo.common.response.ApiResponse<StoreDetailResponse>
}