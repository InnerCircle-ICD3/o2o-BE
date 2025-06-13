package com.eatngo.subscription.docs.controller

import com.eatngo.auth.annotaion.StoreOwnerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.common.response.Cursor
import com.eatngo.docs.ApiResponseErrorDoc
import com.eatngo.subscription.dto.SubscriptionResponseForStoreOwner
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse

@Tag(name = "매장 구독 관리", description = "점주의 매장 구독자 관리 API")
interface StoreOwnerSubscriptionControllerDocs {

    @Operation(
        summary = "매장 구독자 목록 조회 (무한 스크롤)",
        description = """
            특정 매장의 구독자 목록을 커서 기반으로 조회합니다.
            
            **사용법:**
            1. 첫 번째 요청: lastId 없이 요청
            2. 다음 페이지: 이전 응답의 lastId를 사용하여 요청
            3. lastId가 null이면 마지막 페이지
            
            **페이징:** 10개씩 반환, ID 내림차순 정렬
        """
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "매장 구독자 목록 조회 성공",
                content = [Content(schema = Schema(implementation = CursoredStoreOwnerSubscriptionApiResponseDoc::class))]
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

        @StoreOwnerId storeOwnerId: Long,
        
        @Parameter(
            description = "커서 ID (이전 응답의 lastId 값, 첫 번째 요청시에는 생략)",
            required = false,
            example = "123"
        )
        @RequestParam(required = false) lastId: Long?
    ): ApiResponse<Cursor<SubscriptionResponseForStoreOwner>>
}

/**
 * 커서 기반 점주용 구독자 목록 응답 문서
 */
@Schema(description = "커서 기반 구독자 목록 응답")
data class CursoredStoreOwnerSubscriptionApiResponseDoc(
    @Schema(description = "요청 성공 여부", example = "true")
    val success: Boolean = true,
    
    @Schema(description = "커서 기반 응답 데이터")
    val data: StoreOwnerCursorDataDoc
)

@Schema(description = "점주용 커서 데이터")
data class StoreOwnerCursorDataDoc(
    @Schema(description = "구독자 목록")
    val contents: List<SubscriptionResponseForStoreOwnerDoc>,
    
    @Schema(
        description = "다음 페이지 요청용 커서 ID (null이면 마지막 페이지)",
        example = "456",
        nullable = true
    )
    val lastId: Long?
)

@Schema(description = "구독자 정보")
data class SubscriptionResponseForStoreOwnerDoc(
    @Schema(description = "구독 ID", example = "1001")
    val id: Long,
    @Schema(description = "매장 ID", example = "123")
    val storeId: Long,
    @Schema(description = "구독한 사용자 ID", example = "456")
    val userId: Long,
    @Schema(description = "구독 여부", example = "true")
    val subscribed: Boolean,
    @Schema(description = "구독/해제 시간", example = "2025-01-15T10:30:00")
    val actionTime: String
)