package com.eatngo.store.controller

import com.eatngo.auth.annotaion.StoreOwnerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.store.dto.StoreCreateRequest
import com.eatngo.store.dto.StoreDetailResponse
import com.eatngo.store.dto.StoreCUDResponse
import com.eatngo.store.dto.StorePickUpInfoRequest
import com.eatngo.store.dto.StoreUpdateRequest
import com.eatngo.store.service.StoreService
import com.eatngo.store.usecase.StoreOwnerStatusChangeUseCase
import com.eatngo.store.usecase.StoreOwnerStoreCreatedUseCase
import com.eatngo.store.usecase.StoreOwnerStoreDeletedUseCase
import com.eatngo.store.usecase.StoreOwnerStoreUpdatedUseCase
import com.eatngo.store.usecase.StoreQueryUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/stores")
@Tag(name = "상점 API", description = "상점 관련 API")
class StoreController(
    private val storeOwnerStoreCreatedUseCase: StoreOwnerStoreCreatedUseCase,
    private val storeOwnerStoreUpdatedUseCase: StoreOwnerStoreUpdatedUseCase,
    private val storeOwnerStatusChangeUseCase: StoreOwnerStatusChangeUseCase,
    private val storeOwnerStoreDeletedUseCase: StoreOwnerStoreDeletedUseCase,
    private val storeQueryUseCase: StoreQueryUseCase
) {
    @Operation(summary = "상점 상세 조회", description = "점주가 운영중인 자신의 상점 상세 정보를 조회합니다.")
    @GetMapping
    fun getStoresByOwnerId(@StoreOwnerId storeOwnerId: Long): ApiResponse<List<StoreDetailResponse>> {
        val storeDtos = storeQueryUseCase.getStoresByStoreOwnerId(storeOwnerId)
        val responses = storeDtos.map { StoreDetailResponse.fromStoreDto(it) }
        return ApiResponse.success(responses)
    }

    @Operation(summary = "상점 등록", description = "점주가 상점을 등록합니다.")
    @PostMapping
    fun createStore(@RequestBody request: StoreCreateRequest, @StoreOwnerId storeOwnerId: Long): ApiResponse<StoreCUDResponse> {
        val storeDto = storeOwnerStoreCreatedUseCase.create(request.toStoreCreateDto(storeOwnerId))
        return ApiResponse.success(StoreCUDResponse(storeId = storeDto.storeId, actionTime = storeDto.createdAt))
    }
    
    @Operation(summary = "상점 수정", description = "점주가 상점 정보를 수정합니다.")
    @PutMapping("/{storeId}")
    fun updateStore(@PathVariable storeId: Long, @RequestBody request: StoreUpdateRequest, @StoreOwnerId storeOwnerId: Long): ApiResponse<StoreCUDResponse> {
        val storeDto = storeOwnerStoreUpdatedUseCase.update(storeId, storeOwnerId, request.toStoreUpdateDto(storeOwnerId))
        return ApiResponse.success(StoreCUDResponse(storeId = storeDto.storeId, actionTime = storeDto.updatedAt))
    }
    
    @Operation(summary = "상점 상태 변경", description = "점주가 상점의 영업 상태를 변경합니다.")
    @PatchMapping("/{storeId}/status")
    fun updateStoreOnlyStatus(@PathVariable storeId: Long, @RequestBody request: String, @StoreOwnerId storeOwnerId: Long): ApiResponse<StoreCUDResponse> {
        val storeDto = storeOwnerStatusChangeUseCase.change(storeId, storeOwnerId, request)
        return ApiResponse.success(StoreCUDResponse(storeId = storeDto.storeId, actionTime = storeDto.updatedAt))
    }

    //TODO: 픽업관련 정보는 매장 정보 변경 시 한번에 변경할 가능성이 높아서(Update와 통일될 가능성) 주석처리, UI 확정 후 삭제 및 수정
//    @Operation(summary = "상점 픽업 정보 변경", description = "점주가 상점의 픽업 관련 정보를 변경합니다.")
//    @PatchMapping("/{storeId}/pickup-info")
//    fun updateStorePickupInfo(@PathVariable storeId: Long, @RequestBody request: StorePickUpInfoRequest, @StoreOwnerId storeOwnerId: Long): ApiResponse<StoreCUDResponse> {
//         val response = storeService.updateStorePickupInfo(storeId, StorePickUpInfoRequest.from(request), storeOwnerId)
//         return ApiResponse.success(StoreCUDResponse(storeId = response.storeId, actionTime = LocalDateTime.now()))
//    }
    
    @Operation(summary = "상점 삭제", description = "점주가 등록된 상점을 삭제합니다.")
    @DeleteMapping("/{storeId}")
    fun deleteStore(@PathVariable storeId: Long, @StoreOwnerId storeOwnerId: Long): ApiResponse<StoreCUDResponse> {
        val storeDto = storeOwnerStoreDeletedUseCase.delete(storeId, storeOwnerId)
        return ApiResponse.success(StoreCUDResponse(storeId = storeDto.storeId, actionTime = storeDto.deletedAt))
    }
} 