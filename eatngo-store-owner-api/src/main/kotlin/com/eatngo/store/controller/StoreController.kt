package com.eatngo.store.controller

import com.eatngo.common.response.ApiResponse
import com.eatngo.store.dto.PickUpInfoDto
import com.eatngo.store.dto.StoreCreateRequest
import com.eatngo.store.dto.StoreDetailResponse
import com.eatngo.store.dto.StoreDto
import com.eatngo.store.dto.StoreUpdateRequest
import com.eatngo.store.dto.extension.toCoreDto
import com.eatngo.store.dto.extension.toDetailResponse
import com.eatngo.store.service.StoreService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/stores")
@Tag(name = "상점 API", description = "상점 관련 API")
class StoreController(
    private val storeService: StoreService
) {
    @Operation(summary = "상점 등록", description = "점주가 상점을 등록합니다.")
    @PostMapping
    suspend fun createStore(
        @RequestBody request: StoreCreateRequest,
    ): ApiResponse<StoreDto> {
        val storeOwnerId = 1L  //TODO: 현재 로그인 점주 아이디 추후 가져오기
        val coreDto = request.toCoreDto(storeOwnerId)
        val response = storeService.createStore(coreDto)
        return ApiResponse.success(response)
    }
    
    @Operation(summary = "상점 수정", description = "점주가 상점 정보를 수정합니다.")
    @PutMapping("/{storeId}")
    suspend fun updateStore(
        @PathVariable storeId: Long,
        @RequestBody request: StoreUpdateRequest
    ): ApiResponse<StoreDto> {
        val coreDto = request.toCoreDto()
        val response = storeService.updateStore(storeId, coreDto)
        return ApiResponse.success(response)
    }
    
    @Operation(summary = "상점 상태 변경", description = "점주가 상점의 영업 상태를 변경합니다.")
    @PatchMapping("/{storeId}/status")
    suspend fun updateStoreStatus(
        @PathVariable storeId: Long,
        @RequestBody request: String,
    ): ApiResponse<StoreDto> {
        val response = storeService.updateStoreStatus(storeId, request)
        return ApiResponse.success(response)
    }
    
    @Operation(summary = "상점 픽업 정보 변경", description = "점주가 상점의 픽업 관련 정보를 변경합니다.")
    @PatchMapping("/{storeId}/pickup-info")
    suspend fun updateStorePickupInfo(
        @PathVariable storeId: Long,
        @RequestBody request: PickUpInfoDto,
    ): ApiResponse<StoreDto> {
        val response = storeService.updateStorePickupInfo(storeId, request)
        return ApiResponse.success(response)
    }
    
//    @Operation(summary = "상점 삭제", description = "점주가 등록된 상점을 삭제합니다.")
//    @DeleteMapping("/{storeId}")
//    suspend fun deleteStore(
//        @PathVariable storeId: Long,
//    ): ApiResponse<Boolean> {
//        val response = storeService.deleteStore(storeId)
//        return ApiResponse.success(response)
//    }
    
    @Operation(summary = "상점 상세 조회", description = "등록된 상점 상세 정보를 조회합니다.")
    @GetMapping("/{storeId}")
    suspend fun getStoreDetail(
        @PathVariable storeId: Long
    ): ApiResponse<StoreDetailResponse> {
        val storeDto = storeService.getStoreDetail(storeId)
        val response = storeDto.toDetailResponse()
        return ApiResponse.success(response)
    }
} 