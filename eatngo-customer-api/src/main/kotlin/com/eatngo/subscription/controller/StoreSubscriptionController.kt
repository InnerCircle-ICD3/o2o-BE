package com.eatngo.subscription.controller

import com.eatngo.auth.annotation.CustomerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.subscription.dto.StoreSubscriptionResponse
import com.eatngo.subscription.dto.SubscriptionToggleResponse
import com.eatngo.subscription.usecase.CustomerSubscriptionQueryUseCase
import com.eatngo.subscription.usecase.CustomerSubscriptionToggleUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/store-subscriptions")
@Tag(name = "상점 구독 API", description = "상점 구독 관련 API")
class CustomerStoreSubscriptionController(
    private val customerSubscriptionToggleUseCase: CustomerSubscriptionToggleUseCase,
    private val customerSubscriptionQueryUseCase: CustomerSubscriptionQueryUseCase
) {
    @Operation(summary = "구독 토글", description = "고객이 매장 구독을 생성/해제합니다.")
    @PostMapping("/{storeId}")
    fun toggleSubscription(
        @PathVariable storeId: Long,
        @CustomerId customerId: Long
    ): ApiResponse<SubscriptionToggleResponse> {
         val response = customerSubscriptionToggleUseCase.toggle(storeId, customerId)
         return ApiResponse.success(SubscriptionToggleResponse.from(response))
    }

    @Operation(summary = "내 구독 목록 조회", description = "고객이 구독한 모든 매장 목록을 조회합니다.")
    @GetMapping("/me")
    fun getMySubscriptions(
        @CustomerId customerId: Long
    ): ApiResponse<List<StoreSubscriptionResponse>> {
         val subscriptions = customerSubscriptionQueryUseCase.getSubscriptionsByCustomerId(customerId)
         return ApiResponse.success(subscriptions.map { StoreSubscriptionResponse.from(it) })
    }
}