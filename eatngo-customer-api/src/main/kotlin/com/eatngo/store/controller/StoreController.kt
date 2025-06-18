package com.eatngo.store.controller

import com.eatngo.auth.annotation.CustomerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.store.docs.controller.StoreCustomerControllerDocs
import com.eatngo.store.dto.StoreDetailResponse
import com.eatngo.store.usecase.StoreQueryUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/stores")
class StoreController(
    private val storeQueryUseCase: StoreQueryUseCase
) : StoreCustomerControllerDocs {
    @GetMapping("/{storeId}")
    override fun getStoreById(@PathVariable storeId: Long, @CustomerId customerId: Long): ApiResponse<StoreDetailResponse> {
        val (storeDto, isFavorite) = storeQueryUseCase.getStoreByIdWithSubscription(storeId, customerId)
        val response = StoreDetailResponse.from(storeDto, isFavorite)
        return ApiResponse.success(response)
    }
} 