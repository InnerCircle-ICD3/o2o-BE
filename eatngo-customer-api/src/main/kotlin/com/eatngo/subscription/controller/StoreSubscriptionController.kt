package com.eatngo.subscription.controller

import com.eatngo.auth.annotation.CustomerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.subscription.docs.controller.StoreSubscriptionControllerDocs
import com.eatngo.subscription.dto.StoreSubscriptionResponse
import com.eatngo.subscription.dto.SubscriptionToggleResponse
import com.eatngo.subscription.usecase.CustomerSubscriptionQueryUseCase
import com.eatngo.subscription.usecase.CustomerSubscriptionToggleUseCase
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/store-subscriptions")

class CustomerStoreSubscriptionController(
    private val customerSubscriptionToggleUseCase: CustomerSubscriptionToggleUseCase,
    private val customerSubscriptionQueryUseCase: CustomerSubscriptionQueryUseCase
) : StoreSubscriptionControllerDocs {
    @PostMapping("/{storeId}")
    override fun toggleSubscription(
        @PathVariable storeId: Long,
        @CustomerId customerId: Long
    ): ApiResponse<SubscriptionToggleResponse> {
         val response = customerSubscriptionToggleUseCase.toggle(storeId, customerId)
         return ApiResponse.success(SubscriptionToggleResponse.from(response))
    }

    @GetMapping("/me")
    override fun getMySubscriptions(
        @CustomerId customerId: Long
    ): ApiResponse<List<StoreSubscriptionResponse>> {
         val subscriptions = customerSubscriptionQueryUseCase.getSubscriptionsByCustomerId(customerId)
         return ApiResponse.success(subscriptions.map { StoreSubscriptionResponse.from(it) })
    }
}