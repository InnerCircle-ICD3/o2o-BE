package com.eatngo.store.controller

import com.eatngo.common.response.ApiResponse
import com.eatngo.store.dto.StoreSubscriptionResponse
import com.eatngo.store.dto.SubscriptionToggleResponse
import com.eatngo.store.mock.StoreMockData
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
    @Operation(summary = "구독 토글", description = "고객이 매장 구독을 생성/해제합니다.")
    @PostMapping("/{storeId}")
    suspend fun toggleSubscription(
        @PathVariable storeId: Long
    ): ApiResponse<SubscriptionToggleResponse> {
        // TODO: 실제 서비스 로직으로 교체 필요
        // val response = storeSubscriptionService.toggleSubscription(storeId)
        // return ApiResponse.success(SubscriptionToggleResponse.from(response))
        val response = StoreMockData.createSubscriptionToggleResponse(storeId)
        return ApiResponse.success(response)
    }

    @Operation(summary = "내 구독 목록 조회", description = "고객이 구독한 모든 매장 목록을 조회합니다.")
    @GetMapping("/me")
    suspend fun getMySubscriptions(): ApiResponse<List<StoreSubscriptionResponse>> {
        // TODO: 실제 서비스 로직으로 교체 필요
        // val subscriptions = storeSubscriptionService.getMySubscriptions()
        // return ApiResponse.success(subscriptions.map { StoreSubscriptionResponse.from(it) })
        val response = listOf(
            StoreMockData.createStoreSubscriptionResponse(1),
            StoreMockData.createStoreSubscriptionResponse(2),
            StoreMockData.createStoreSubscriptionResponse(3)
        )
        return ApiResponse.success(response)
    }
}