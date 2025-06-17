package com.eatngo.subscription.controller

import com.eatngo.auth.annotation.CustomerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.common.response.Cursor
import com.eatngo.subscription.docs.controller.StoreSubscriptionControllerDocs
import com.eatngo.subscription.dto.CustomerSubscriptionQueryParamDto
import com.eatngo.subscription.dto.StoreSubscriptionResponse
import com.eatngo.subscription.dto.SubscriptionToggleResponse
import com.eatngo.subscription.service.StoreSubscriptionService
import com.eatngo.subscription.usecase.CustomerSubscriptionQueryUseCase
import com.eatngo.subscription.usecase.CustomerSubscriptionToggleUseCase
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/store-subscriptions")

class CustomerStoreSubscriptionController(
    private val customerSubscriptionToggleUseCase: CustomerSubscriptionToggleUseCase,
    private val customerSubscriptionQueryUseCase: CustomerSubscriptionQueryUseCase,
    private val storeSubscriptionService: StoreSubscriptionService
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
        @CustomerId customerId: Long,
        @RequestParam(required = false) lastId: Long?
    ): ApiResponse<Cursor<StoreSubscriptionResponse>> {
        val queryParam = CustomerSubscriptionQueryParamDto(
            customerId = customerId,
            lastId = lastId
        )
        val cursoredSubscriptions = customerSubscriptionQueryUseCase.getSubscriptionsByQueryParameter(queryParam)

        val response = Cursor.from(
            cursoredSubscriptions.contents.map { StoreSubscriptionResponse.from(it) },
            cursoredSubscriptions.lastId
        )
        return ApiResponse.success(response)
    }

    @GetMapping("/store-ids")
    override fun getSubscribedStoreIds(@CustomerId customerId: Long): ApiResponse<List<Long>> {
        val response = storeSubscriptionService.getSubscribedStoreIds(customerId)
        return ApiResponse.success(response)
    }
}