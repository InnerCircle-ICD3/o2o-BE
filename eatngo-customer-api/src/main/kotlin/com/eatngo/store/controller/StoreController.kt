package com.eatngo.store.controller

import com.eatngo.common.response.ApiResponse
import com.eatngo.store.dto.StoreDetailResponse
import com.eatngo.store.mock.StoreMockData
import com.eatngo.store.service.StoreService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/stores")
@Tag(name = "상점 API", description = "상점 관련 API")
class StoreController(
    private val storeService: StoreService
) {
    @Operation(summary = "상점 상세 조회", description = "상점 상세 정보를 조회합니다.")
    @GetMapping("/{storeId}")
    suspend fun getStoreDetail(
        @PathVariable storeId: Long
    ): ApiResponse<StoreDetailResponse> {
        // TODO: 실제 서비스 로직으로 교체 필요
        // val storeDto = storeService.getStoreDetail(storeId)
        // val response = StoreDetailResponse.from(storeDto)
        val response = StoreMockData.createStoreDetailResponse(storeId)
        return ApiResponse.success(response)
    }
} 