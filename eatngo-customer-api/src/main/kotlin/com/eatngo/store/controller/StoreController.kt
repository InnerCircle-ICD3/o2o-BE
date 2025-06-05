package com.eatngo.store.controller

import com.eatngo.common.response.ApiResponse
import com.eatngo.store.docs.StoreCustomerControllerDocs
import com.eatngo.store.dto.StoreDetailResponse
import com.eatngo.store.usecase.StoreQueryUseCase
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/stores")

class StoreController(
    private val storeQueryUseCase: StoreQueryUseCase
) : StoreCustomerControllerDocs {
    @GetMapping("/{storeId}")
    override fun getStoreById(@PathVariable storeId: Long): ApiResponse<StoreDetailResponse> {
        val storeDto = storeQueryUseCase.getStoreById(storeId)
        val response = StoreDetailResponse.from(storeDto)
        return ApiResponse.success(response)
    }
} 