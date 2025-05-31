package com.eatngo.subscription.controller

import com.eatngo.auth.annotaion.StoreOwnerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.subscription.dto.SubscriptionResponseForStoreOwner
import com.eatngo.subscription.usecase.StoreOwnerSubscriptionQueryUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/store-subscriptions")
@Tag(name = "상점 구독 API", description = "상점 구독 관련 API")
class StoreOwnerSubscriptionController(
    private val storeOwnerSubscriptionQueryUseCase: StoreOwnerSubscriptionQueryUseCase
) {
    @Operation(summary = "매장별 구독 목록 조회", description = "점주가 운영중인 매장을 구독하고 있는 구독자 목록을 조회합니다.")
    @GetMapping("/stores/{storeId}")
    fun getStoreSubscriptions(
        @PathVariable storeId: Long, 
        @StoreOwnerId storeOwnerId: Long
    ): ApiResponse<List<SubscriptionResponseForStoreOwner>> {
         val response = storeOwnerSubscriptionQueryUseCase.getSubscriptionsByStoreId(storeId, storeOwnerId)
         return ApiResponse.success(response.map { SubscriptionResponseForStoreOwner.fromStoreSubscriptionDto(it) })
    }
    
    @Operation(summary = "매장 구독자 수 조회", description = "점주가 운영중인 매장의 구독자 수를 조회합니다.")
    @GetMapping("/stores/{storeId}/count")
    fun getStoreSubscriptionCount(
        @PathVariable storeId: Long, 
        @StoreOwnerId storeOwnerId: Long
    ): ApiResponse<Int> {
         val count = storeOwnerSubscriptionQueryUseCase.getSubscriptionCountByStoreId(storeId, storeOwnerId)
         return ApiResponse.success(count)
    }
}