package com.eatngo.store.controller

import com.eatngo.common.response.ApiResponse
import com.eatngo.store.dto.StoreDetailResponse
import com.eatngo.store.usecase.StoreQueryUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/stores")
@Tag(name = "상점 API", description = "상점 관련 API")
class StoreController(
    private val storeQueryUseCase : StoreQueryUseCase
) {
    @Operation(summary = "상점 상세 조회", description = "상점 상세 정보를 조회합니다.")
    @GetMapping("/{storeId}")
    fun getStoreById(
        @PathVariable storeId: Long
    ): ApiResponse<StoreDetailResponse> {
         val storeDto = storeQueryUseCase.getStoreById(storeId)
         val response = StoreDetailResponse.from(storeDto)
        return ApiResponse.success(response)
    }
} 