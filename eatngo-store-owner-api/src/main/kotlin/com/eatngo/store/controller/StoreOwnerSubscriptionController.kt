package com.eatngo.store.controller

import com.eatngo.auth.annotaion.StoreOwnerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.store.docs.StoreOwnerSubscriptionDocs
import com.eatngo.store.docs.StoreSubscriberResponse
import com.eatngo.store.docs.StoreSubscriptionStats
import com.eatngo.store.usecase.StoreOwnerSubscriptionUseCase
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/stores")
class StoreOwnerSubscriptionController(
    private val storeOwnerSubscriptionUseCase: StoreOwnerSubscriptionUseCase
) : StoreOwnerSubscriptionDocs {

    @GetMapping("/{storeId}/subscribers")
    override fun getStoreSubscribers(
        @PathVariable storeId: Long,
        @StoreOwnerId storeOwnerId: Long
    ): ApiResponse<List<StoreSubscriberResponse>> {
        val subscribers = storeOwnerSubscriptionUseCase.getStoreSubscribers(storeId, storeOwnerId)
        return ApiResponse.success(
            subscribers.map {
                StoreSubscriberResponse(
                    subscriberId = it.subscriberId,
                    nickname = it.nickname,
                    subscribedAt = it.subscribedAt.toString(),
                    lastOrderAt = it.lastOrderAt?.toString()
                )
            }
        )
    }

    @GetMapping("/{storeId}/subscription-stats")
    override fun getStoreSubscriptionStats(
        @PathVariable storeId: Long,
        @StoreOwnerId storeOwnerId: Long
    ): ApiResponse<StoreSubscriptionStats> {
        val stats = storeOwnerSubscriptionUseCase.getStoreSubscriptionStats(storeId, storeOwnerId)
        return ApiResponse.success(
            StoreSubscriptionStats(
                totalSubscribers = stats.totalSubscribers,
                newSubscribersThisMonth = stats.newSubscribersThisMonth,
                unsubscribersThisMonth = stats.unsubscribersThisMonth,
                averageOrdersPerSubscriber = stats.averageOrdersPerSubscriber
            )
        )
    }
} 