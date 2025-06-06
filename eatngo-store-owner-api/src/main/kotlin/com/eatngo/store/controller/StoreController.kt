package com.eatngo.store.controller

import com.eatngo.auth.annotaion.StoreOwnerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.store.docs.controller.StoreOwnerControllerDocs
import com.eatngo.store.dto.*
import com.eatngo.store.usecase.StoreCUDUseCase
import com.eatngo.store.usecase.StoreOwnerStatusChangeUseCase
import com.eatngo.store.usecase.StoreQueryUseCase
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/stores")
class StoreController(
    private val storeCUDUseCase: StoreCUDUseCase,
    private val storeOwnerStatusChangeUseCase: StoreOwnerStatusChangeUseCase,
    private val storeQueryUseCase: StoreQueryUseCase
) : StoreOwnerControllerDocs {

    @GetMapping
    override fun getStoresByOwnerId(@StoreOwnerId storeOwnerId: Long): ApiResponse<List<StoreDetailResponse>> {
        val storeDtos = storeQueryUseCase.getStoresByStoreOwnerId(storeOwnerId)
        val responses = storeDtos.map { StoreDetailResponse.fromStoreDto(it) }
        return ApiResponse.success(responses)
    }

    @PostMapping
    override fun createStore(@RequestBody request: StoreCreateRequest, @StoreOwnerId storeOwnerId: Long): ApiResponse<StoreCUDResponse> {
        val storeDto = storeCUDUseCase.createStore(request.toStoreCreateDto(storeOwnerId))
        return ApiResponse.success(StoreCUDResponse(storeId = storeDto.storeId, actionTime = storeDto.createdAt))
    }
    
    @PutMapping("/{storeId}")
    override fun updateStore(
        @PathVariable storeId: Long,
        @RequestBody request: StoreUpdateRequest,
        @StoreOwnerId storeOwnerId: Long
    ): ApiResponse<StoreCUDResponse> {
        val storeDto = storeCUDUseCase.updateStore(storeId, storeOwnerId, request.toStoreUpdateDto(storeOwnerId))
        return ApiResponse.success(StoreCUDResponse(storeId = storeDto.storeId, actionTime = storeDto.updatedAt))
    }
    
    @PatchMapping("/{storeId}/status")
    override fun updateStoreOnlyStatus(
        @PathVariable storeId: Long,
        @RequestBody request: StoreStatusUpdateRequest,
        @StoreOwnerId storeOwnerId: Long
    ): ApiResponse<StoreCUDResponse> {
        val storeDto = storeOwnerStatusChangeUseCase.change(storeId, storeOwnerId, request.status)
        return ApiResponse.success(StoreCUDResponse(storeId = storeDto.storeId, actionTime = storeDto.updatedAt))
    }

    @DeleteMapping("/{storeId}")
    override fun deleteStore(@PathVariable storeId: Long, @StoreOwnerId storeOwnerId: Long): ApiResponse<StoreCUDResponse> {
        val response = storeCUDUseCase.deleteStore(storeId, storeOwnerId)
        return ApiResponse.success(StoreCUDResponse(storeId = response, actionTime = LocalDateTime.now()))
    }
} 