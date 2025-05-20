package com.eatngo.store.controller

import com.eatngo.common.response.ApiResponse
import com.eatngo.store.dto.StoreSubscriptionDto
import com.eatngo.store.dto.StoreSubscriptionResponse
import com.eatngo.store.dto.SubscriptionToggleResponse
import com.eatngo.store.dto.extension.toResponse
import com.eatngo.store.service.StoreSubscriptionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/store-subscriptions")
@Tag(name = "상점 구독 API", description = "상점 구독 관련 API")
class StoreSubscriptionController(
    private val storeSubscriptionService: StoreSubscriptionService,
) {
    @Operation(summary = "구독 토글", description = "매장 구독을 생성/해제합니다.")
    @PostMapping("/stores/{storeId}")
    suspend fun toggleSubscription(
        @PathVariable storeId: Long
    ): ApiResponse<SubscriptionToggleResponse> {
        val dto = storeSubscriptionService.toggleSubscription(storeId)
        return ApiResponse.success(
            SubscriptionToggleResponse(
                id = dto.id,
                storeId = dto.storeId,
                userId = dto.userId,
                subscribed = dto.deletedAt == null,
                actionTime = dto.updatedAt
            )
        )
    }

    @Operation(summary = "내 구독 목록 조회", description = "사용자가 구독한 모든 매장 목록을 조회합니다.")
    @GetMapping("/me")
    suspend fun getMySubscriptions(): ApiResponse<List<StoreSubscriptionResponse>> {
        val subscriptions = storeSubscriptionService.getMySubscriptions()
        return ApiResponse.success(
            subscriptions.map { it.toResponse() }
        )
    }
}