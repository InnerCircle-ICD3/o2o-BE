package com.eatngo.store.controller

import com.eatngo.auth.annotaion.StoreOwnerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.store.dto.PickUpInfoDto
import com.eatngo.store.dto.StoreCreateRequest
import com.eatngo.store.dto.StoreDetailResponse
import com.eatngo.store.dto.StoreCUDResponse
import com.eatngo.store.dto.StoreUpdateRequest
import com.eatngo.store.mock.StoreMockData
import com.eatngo.store.service.StoreService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/stores")
@Tag(name = "상점 API", description = "상점 관련 API")
class StoreController(
    private val storeService: StoreService
) {
    @Operation(summary = "상점 등록", description = "점주가 상점을 등록합니다.")
    @PostMapping
    fun createStore(@RequestBody request: StoreCreateRequest, @StoreOwnerId storeOwnerId: Long): ApiResponse<StoreCUDResponse> {
        // TODO: 실제 서비스 로직으로 교체 필요
        // val response = storeService.createStore(request.toStoreCreateDto(storeOwnerId))
        // return ApiResponse.success(StoreCUDResponse(storeId = response.storeId, actionTime = LocalDateTime.now()))
        val response = StoreMockData.createStoreCUDResponse(1)
        return ApiResponse.success(response)
    }
    
    @Operation(summary = "상점 수정", description = "점주가 상점 정보를 수정합니다.")
    @PutMapping("/{storeId}")
    fun updateStore(@PathVariable storeId: Long, @RequestBody request: StoreUpdateRequest, @StoreOwnerId storeOwnerId: Long): ApiResponse<StoreCUDResponse> {
        // TODO: 실제 서비스 로직으로 교체 필요
        // val response = storeService.updateStore(storeId, request.toStoreUpdateDto(storeOwnerId = storeOwnerId))
        // return ApiResponse.success(StoreCUDResponse(storeId = response.storeId, actionTime = LocalDateTime.now()))
        val response = StoreMockData.createStoreCUDResponse(storeId)
        return ApiResponse.success(response)
    }
    
    @Operation(summary = "상점 상태 변경", description = "점주가 상점의 영업 상태를 변경합니다.")
    @PatchMapping("/{storeId}/status")
    fun updateStoreOnlyStatus(@PathVariable storeId: Long, @RequestBody request: String, @StoreOwnerId storeOwnerId: Long): ApiResponse<StoreCUDResponse> {
        // TODO: 실제 서비스 로직으로 교체 필요
        // val response = storeService.updateStoreOnlyStatus(storeId, request)
        // return ApiResponse.success(StoreCUDResponse(storeId = response.storeId, actionTime = LocalDateTime.now()))
        val response = StoreMockData.createStoreCUDResponse(storeId)
        return ApiResponse.success(response)
    }
    
    @Operation(summary = "상점 픽업 정보 변경", description = "점주가 상점의 픽업 관련 정보를 변경합니다.")
    @PatchMapping("/{storeId}/pickup-info")
    fun updateStorePickupInfo(@PathVariable storeId: Long, @RequestBody request: PickUpInfoDto, @StoreOwnerId storeOwnerId: Long): ApiResponse<StoreCUDResponse> {
        // TODO: 실제 서비스 로직으로 교체 필요
        // val response = storeService.updateStorePickupInfo(storeId, request)
        // return ApiResponse.success(StoreCUDResponse(storeId = response.storeId, actionTime = LocalDateTime.now()))
        val response = StoreMockData.createStoreCUDResponse(storeId)
        return ApiResponse.success(response)
    }
    
    @Operation(summary = "상점 삭제", description = "점주가 등록된 상점을 삭제합니다.")
    @DeleteMapping("/{storeId}")
    fun deleteStore(@PathVariable storeId: Long, @StoreOwnerId storeOwnerId: Long): ApiResponse<StoreCUDResponse> {
        // TODO: 실제 서비스 로직으로 교체 필요
        // val response = storeService.deleteStore(storeId)
        // return ApiResponse.success(StoreCUDResponse(storeId = response.storeId, actionTime = response.deletedAt))
        val response = StoreMockData.createStoreCUDResponse(storeId)
        return ApiResponse.success(response)
    }
    
    @Operation(summary = "상점 상세 조회", description = "점주가 등록된 상점 상세 정보를 조회합니다.")
    @GetMapping("/{storeId}")
    fun getStoreDetail(@PathVariable storeId: Long, @StoreOwnerId storeOwnerId: Long): ApiResponse<StoreDetailResponse> {
        // TODO: 실제 서비스 로직으로 교체 필요
        // val storeDto = storeService.getStoreDetail(storeId)
        // return ApiResponse.success(StoreDetailResponse.fromStoreDto(storeDto))
        val response = StoreMockData.createStoreDetailResponse(storeId)
        return ApiResponse.success(response)
    }
} 