package com.eatngo.subscription.controller

import com.eatngo.auth.annotaion.StoreOwnerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.common.response.Cursor
import com.eatngo.subscription.docs.controller.StoreOwnerSubscriptionControllerDocs
import com.eatngo.subscription.dto.StoreOwnerSubscriptionQueryParamDto
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
        @StoreOwnerId storeOwnerId: Long,
        @RequestParam(required = false) lastId: Long?
    ): ApiResponse<Cursor<SubscriptionResponseForStoreOwner>> {
        val queryParam = StoreOwnerSubscriptionQueryParamDto(
            storeId = storeId,
            storeOwnerId = storeOwnerId,
            lastId = lastId
        )
        val cursoredSubscriptions = storeOwnerSubscriptionQueryUseCase.getSubscriptionsByQueryParameter(queryParam)
        
        val response = Cursor.from(
            cursoredSubscriptions.contents.map { SubscriptionResponseForStoreOwner.fromStoreSubscriptionDto(it) },
            cursoredSubscriptions.lastId
        )
        return ApiResponse.success(response)
    }
}