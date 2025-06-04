package com.eatngo.store.controller

import com.eatngo.auth.annotaion.CustomerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.store.docs.StoreSubscriptionDocs
import com.eatngo.store.docs.StoreSubscriptionResponse
import com.eatngo.store.usecase.CustomerStoreSubscriptionUseCase
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/stores")
class CustomerStoreSubscriptionController(
    private val customerStoreSubscriptionUseCase: CustomerStoreSubscriptionUseCase
) : StoreSubscriptionDocs {

    @PostMapping("/{storeId}/subscribe")
    override fun subscribeStore(
        @PathVariable storeId: Long,
        @CustomerId customerId: Long
    ): ApiResponse<StoreSubscriptionResponse> {
        val subscriptionDto = customerStoreSubscriptionUseCase.subscribe(storeId, customerId)
        return ApiResponse.success(
            StoreSubscriptionResponse(
                subscriptionId = subscriptionDto.id,
                storeId = subscriptionDto.storeId,
                storeName = subscriptionDto.storeName,
                storeImageUrl = subscriptionDto.storeImageUrl,
                status = "SUBSCRIBED"
            )
        )
    }

    @DeleteMapping("/{storeId}/subscribe")
    override fun unsubscribeStore(
        @PathVariable storeId: Long,
        @CustomerId customerId: Long
    ): ApiResponse<StoreSubscriptionResponse> {
        val subscriptionDto = customerStoreSubscriptionUseCase.unsubscribe(storeId, customerId)
        return ApiResponse.success(
            StoreSubscriptionResponse(
                subscriptionId = subscriptionDto.id,
                storeId = subscriptionDto.storeId,
                storeName = subscriptionDto.storeName,
                storeImageUrl = subscriptionDto.storeImageUrl,
                status = "UNSUBSCRIBED"
            )
        )
    }

    @GetMapping("/subscriptions")
    override fun getSubscribedStores(
        @CustomerId customerId: Long
    ): ApiResponse<List<StoreSubscriptionResponse>> {
        val subscriptions = customerStoreSubscriptionUseCase.getSubscribedStores(customerId)
        return ApiResponse.success(
            subscriptions.map {
                StoreSubscriptionResponse(
                    subscriptionId = it.id,
                    storeId = it.storeId,
                    storeName = it.storeName,
                    storeImageUrl = it.storeImageUrl,
                    status = "SUBSCRIBED"
                )
            }
        )
    }
} 