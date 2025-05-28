package com.eatngo.store.controller

import com.eatngo.auth.annotaion.StoreOwnerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.store.dto.SubscriptionResponseForStoreOwner
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
    @Operation(summary = "매장별 구독 목록 조회", description = "점주가 운영중인 매장을 구독하고 있는 구독자 목록을 조회합니다.")
    @GetMapping("/stores/{storeId}")
    fun getStoreSubscriptions(@PathVariable storeId: Long, @StoreOwnerId storeOwnerId: Long): ApiResponse<List<SubscriptionResponseForStoreOwner>> {
         val response = storeSubscriptionService.getSubscriptionsByStoreId(storeId)
         return ApiResponse.success(response.map { SubscriptionResponseForStoreOwner.fromStoreSubscriptionDto(it) })
    }
}