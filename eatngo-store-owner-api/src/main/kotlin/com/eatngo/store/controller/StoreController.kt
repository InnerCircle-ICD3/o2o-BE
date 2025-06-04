package com.eatngo.store.controller

import com.eatngo.auth.annotaion.StoreOwnerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.store.docs.StoreOwnerDocs
import com.eatngo.store.dto.*
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
) : StoreOwnerDocs {

    @GetMapping
    @Operation(summary = "상점 상세 조회", description = "점주가 운영중인 자신의 상점 상세 정보를 조회합니다.")
    override fun getStoresByOwnerId(@StoreOwnerId storeOwnerId: Long): ApiResponse<List<StoreDetailResponse>> {
        val storeDtos = storeQueryUseCase.getStoresByStoreOwnerId(storeOwnerId)
        val responses = storeDtos.map { StoreDetailResponse.fromStoreDto(it) }
        return ApiResponse.success(responses)
    }

    @PostMapping
    @Operation(summary = "상점 등록", description = "점주가 상점을 등록합니다.")
    override fun createStore(@RequestBody request: StoreCreateRequest, @StoreOwnerId storeOwnerId: Long): ApiResponse<StoreCUDResponse> {
        val storeDto = storeOwnerStoreCreatedUseCase.create(request.toStoreCreateDto(storeOwnerId))
        return ApiResponse.success(StoreCUDResponse(storeId = storeDto.storeId, actionTime = storeDto.createdAt))
    }
    
    @PutMapping("/{storeId}")
    @Operation(summary = "상점 수정", description = "점주가 상점 정보를 수정합니다.")
    override fun updateStore(
        @PathVariable storeId: Long,
        @RequestBody request: StoreUpdateRequest,
        @StoreOwnerId storeOwnerId: Long
    ): ApiResponse<StoreCUDResponse> {
        val storeDto = storeOwnerStoreUpdatedUseCase.update(storeId, storeOwnerId, request.toStoreUpdateDto(storeOwnerId))
        return ApiResponse.success(StoreCUDResponse(storeId = storeDto.storeId, actionTime = storeDto.updatedAt))
    }
    
    @PatchMapping("/{storeId}/status")
    @Operation(summary = "상점 상태 변경", description = "점주가 상점의 영업 상태를 변경합니다.")
    override fun updateStoreOnlyStatus(
        @PathVariable storeId: Long,
        @RequestBody request: StoreStatusUpdateRequest,
        @StoreOwnerId storeOwnerId: Long
    ): ApiResponse<StoreCUDResponse> {
        val storeDto = storeOwnerStatusChangeUseCase.change(storeId, storeOwnerId, request.status)
        return ApiResponse.success(StoreCUDResponse(storeId = storeDto.storeId, actionTime = storeDto.updatedAt))
    }

    @DeleteMapping("/{storeId}")
    @Operation(summary = "상점 삭제", description = "점주가 등록된 상점을 삭제합니다.")
    override fun deleteStore(@PathVariable storeId: Long, @StoreOwnerId storeOwnerId: Long): ApiResponse<StoreCUDResponse> {
        val response = storeOwnerStoreDeletedUseCase.delete(storeId, storeOwnerId)
        return ApiResponse.success(StoreCUDResponse(storeId = response, actionTime = LocalDateTime.now()))
    }
} 