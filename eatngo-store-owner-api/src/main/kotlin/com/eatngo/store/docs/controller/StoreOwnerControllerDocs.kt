package com.eatngo.store.docs.controller

import com.eatngo.auth.annotaion.StoreOwnerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.docs.ApiResponseErrorDoc
import com.eatngo.store.docs.response.StoreCUDApiResponseDoc
import com.eatngo.store.docs.response.StoreDetailApiResponseDoc
import com.eatngo.store.dto.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse

@Tag(name = "매장 관리", description = "점주의 매장 관리 API")
interface StoreOwnerControllerDocs {

    @Operation(
        summary = "매장 목록 조회",
        description = "점주가 운영 중인 매장 목록을 조회합니다."
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "매장 조회 성공",
                content = [Content(schema = Schema(implementation = StoreDetailApiResponseDoc::class))]
            ),
            SwaggerApiResponse(
                responseCode = "400",
                description = "잘못된 요청(400~500 에러 포함)",
                content = [Content(schema = Schema(implementation = ApiResponseErrorDoc::class))]
            )
        ]
    )
    fun getStoresByOwnerId(
        @StoreOwnerId storeOwnerId: Long
    ): ApiResponse<List<StoreDetailResponse>>

    @Operation(
        summary = "매장 등록",
        description = "새로운 매장을 등록합니다. 매장 등록 시 필수 정보와 선택 정보를 입력받습니다."
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "매장 등록 성공",
                content = [Content(schema = Schema(implementation = StoreCUDApiResponseDoc::class))]
            ),
            SwaggerApiResponse(
                responseCode = "400",
                description = "잘못된 요청(400~500 에러 포함)",
                content = [Content(schema = Schema(implementation = ApiResponseErrorDoc::class))]
            )
        ]
    )
    fun createStore(
        @Parameter(description = "매장 생성 정보", required = true)
        @RequestBody request: StoreCreateRequest,

        @StoreOwnerId storeOwnerId: Long
    ): ApiResponse<StoreCUDResponse>

    @Operation(
        summary = "매장 정보 수정",
        description = "등록된 매장의 정보를 수정합니다. 수정 가능한 모든 필드는 선택적으로 입력 가능합니다."
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "매장 정보 수정 성공",
                content = [Content(schema = Schema(implementation = StoreCUDApiResponseDoc::class))]
            ),
            SwaggerApiResponse(
                responseCode = "400",
                description = "잘못된 요청(400~500 에러 포함)",
                content = [Content(schema = Schema(implementation = ApiResponseErrorDoc::class))]
            )
        ]
    )
    fun updateStore(
        @Parameter(description = "수정할 매장 ID", required = true)
        @PathVariable storeId: Long,

        @Parameter(description = "매장 수정 정보", required = true)
        @RequestBody request: StoreUpdateRequest,

        @StoreOwnerId storeOwnerId: Long
    ): ApiResponse<StoreCUDResponse>

    @Operation(
        summary = "매장 상태 변경",
        description = "매장의 영업 상태를 변경합니다. (OPEN: 영업중, CLOSED: 영업종료, PENDING: 승인대기)"
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "매장 상태 변경 성공",
                content = [Content(schema = Schema(implementation = StoreCUDApiResponseDoc::class))]
            ),
            SwaggerApiResponse(
                responseCode = "400",
                description = "잘못된 요청(400~500 에러 포함)",
                content = [Content(schema = Schema(implementation = ApiResponseErrorDoc::class))]
            )
        ]
    )
    fun updateStoreOnlyStatus(
        @Parameter(description = "상태 변경할 매장 ID", required = true)
        @PathVariable storeId: Long,

        @Parameter(description = "변경할 매장 상태", required = true)
        @RequestBody request: StoreStatusUpdateRequest,

        @StoreOwnerId storeOwnerId: Long
    ): ApiResponse<StoreCUDResponse>

    @Operation(
        summary = "매장 삭제",
        description = "등록된 매장을 삭제합니다."
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "매장 삭제 성공",
                content = [Content(schema = Schema(implementation = StoreCUDApiResponseDoc::class))]
            ),
            SwaggerApiResponse(
                responseCode = "400",
                description = "잘못된 요청(400~500 에러 포함)",
                content = [Content(schema = Schema(implementation = ApiResponseErrorDoc::class))]
            )
        ]
    )
    fun deleteStore(
        @Parameter(description = "삭제할 매장 ID", required = true)
        @PathVariable storeId: Long,

        @StoreOwnerId storeOwnerId: Long
    ): ApiResponse<StoreCUDResponse>
}