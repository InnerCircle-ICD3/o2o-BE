package com.eatngo.subscription.docs.controller

import com.eatngo.auth.annotation.CustomerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.common.response.Cursor
import com.eatngo.docs.ApiResponseErrorDoc
import com.eatngo.subscription.docs.response.SubscriptionToggleApiResponseDoc
import com.eatngo.subscription.dto.StoreSubscriptionResponse
import com.eatngo.subscription.dto.SubscriptionToggleResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse

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
                description = "구독 토글 성공",
                content = [Content(schema = Schema(implementation = SubscriptionToggleApiResponseDoc::class))]
            ),
            SwaggerApiResponse(
                responseCode = "400",
                description = "잘못된 요청(400~500 에러 포함)",
                content = [Content(schema = Schema(implementation = ApiResponseErrorDoc::class))]
            )
        ]
    )
    fun toggleSubscription(
        @Parameter(description = "구독할 매장 ID", required = true)
        @PathVariable storeId: Long,

        @CustomerId customerId: Long
    ): ApiResponse<SubscriptionToggleResponse>

    @Operation(
        summary = "내 구독 매장 목록 조회 (무한 스크롤)",
        description = """
            현재 로그인한 고객이 구독한 매장 목록을 커서 기반으로 조회합니다.
            
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
                description = "내 구독 매장 목록 조회 성공",
                content = [Content(schema = Schema(implementation = CursoredStoreSubscriptionListApiResponseDoc::class))]
            ),
            SwaggerApiResponse(
                responseCode = "400",
                description = "잘못된 요청(400~500 에러 포함)",
                content = [Content(schema = Schema(implementation = ApiResponseErrorDoc::class))]
            )
        ]
    )
    fun getMySubscriptions(
        @CustomerId customerId: Long,
        
        @Parameter(
            description = "커서 ID (이전 응답의 lastId 값, 첫 번째 요청시에는 생략)",
            required = false,
            example = "123"
        )
        @RequestParam(required = false) lastId: Long?
    ): ApiResponse<Cursor<StoreSubscriptionResponse>>
}

/**
 * 커서 기반 구독 목록 응답 문서
 */
@Schema(description = "커서 기반 구독 목록 응답")
data class CursoredStoreSubscriptionListApiResponseDoc(
    @Schema(description = "요청 성공 여부", example = "true")
    val success: Boolean = true,
    
    @Schema(description = "커서 기반 응답 데이터")
    val data: CursorDataDoc
)

@Schema(description = "커서 데이터")
data class CursorDataDoc(
    @Schema(description = "구독 목록")
    val contents: List<StoreSubscriptionResponseDoc>,
    
    @Schema(
        description = "다음 페이지 요청용 커서 ID (null이면 마지막 페이지)",
        example = "456",
        nullable = true
    )
    val lastId: Long?
)

@Schema(description = "구독 매장 정보")
data class StoreSubscriptionResponseDoc(
    @Schema(description = "구독 ID", example = "1001")
    val id: Long,
    @Schema(description = "매장 ID", example = "123")
    val storeId: Long,
    @Schema(description = "매장명", example = "맛있는 분식집")
    val storeName: String,
    @Schema(description = "매장 설명", example = "신선한 재료로 만든 김밥 전문점")
    val description: String?,
    @Schema(description = "매장 대표 이미지", example = "https://example.com/image.jpg")
    val mainImageUrl: String?,
    @Schema(description = "음식 카테고리", example = "[\"김밥\", \"떡볶이\"]")
    val foodCategory: List<String>?,
    @Schema(description = "매장 상태", example = "OPEN")
    val status: String,
    @Schema(description = "할인율", example = "0.1")
    val discountRate: Double?,
    @Schema(description = "원가", example = "12000")
    val originalPrice: Int?,
    @Schema(description = "할인된 가격", example = "10800")
    val discountedPrice: Int?,
    @Schema(description = "오늘 픽업 시작 시간", example = "09:00:00")
    val todayPickupStartTime: String?,
    @Schema(description = "오늘 픽업 종료 시간", example = "18:00:00")
    val todayPickupEndTime: String?,
    @Schema(description = "총 재고 수량", example = "25")
    val totalStockCount: Int?,
    @Schema(description = "픽업 가능 요일", example = "TODAY")
    val pickupDay: String?,
    @Schema(description = "구독 일시", example = "2025-01-15T10:30:00")
    val subscribedAt: String
)