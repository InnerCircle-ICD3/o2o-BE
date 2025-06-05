package com.eatngo.subscription.controller

import com.eatngo.auth.annotaion.StoreOwnerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.subscription.docs.controller.StoreOwnerSubscriptionControllerDocs
import com.eatngo.subscription.dto.SubscriptionResponseForStoreOwner
import com.eatngo.subscription.usecase.StoreOwnerSubscriptionQueryUseCase
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/store-subscriptions")
class StoreOwnerSubscriptionController(
    private val storeOwnerSubscriptionQueryUseCase: StoreOwnerSubscriptionQueryUseCase
) : StoreOwnerSubscriptionControllerDocs {
    @GetMapping("/stores/{storeId}")
    override fun getStoreSubscriptions(
        @PathVariable storeId: Long, 
        @StoreOwnerId storeOwnerId: Long
    ): ApiResponse<List<SubscriptionResponseForStoreOwner>> {
         val response = storeOwnerSubscriptionQueryUseCase.getSubscriptionsByStoreId(storeId, storeOwnerId)
         return ApiResponse.success(response.map { SubscriptionResponseForStoreOwner.fromStoreSubscriptionDto(it) })
    }
}