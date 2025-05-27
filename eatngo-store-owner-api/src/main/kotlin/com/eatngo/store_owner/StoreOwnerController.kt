package com.eatngo.store_owner

import com.eatngo.auth.annotaion.StoreOwnerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.store_owner.dto.StoreOwnerDto
import com.eatngo.store_owner.dto.StoreOwnerUpdateDto
import com.eatngo.store_owner.service.StoreOwnerService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@Tag(name = "StoreOwner", description = "유저 정보를 관리하는 API")
@RestController
@RequestMapping("/api/v1/store-owners")
class StoreOwnerController(
    private val storeOwnerService: StoreOwnerService,
) {
    @Operation(summary = "고객 정보 조회 API", description = "고객 정보를 조회하는 API")
    @GetMapping("/me")
    fun getCustomer(
        @StoreOwnerId storeOwnerId: Long,
    ): ResponseEntity<ApiResponse<StoreOwnerDto>> {
        val storeOwnerDto = storeOwnerService.getStoreOwnerById(storeOwnerId)
            .let { StoreOwnerDto.from(it) }
        return ResponseEntity.ok(ApiResponse.success(storeOwnerDto))
    }

    @Operation(summary = "탈퇴 API", description = "유저의 정보를 삭제하는 API")
    @DeleteMapping("/sign-out")
    fun deleteCustomer(
        @StoreOwnerId storeOwnerId: Long,
    ): ResponseEntity<Unit> {
        storeOwnerService.deleteStoreOwner(storeOwnerId)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "고객 정보 수정 API", description = "유저의 정보를 수정하는 API")
    @PatchMapping("/modify")
    fun updateCustomer(
        @StoreOwnerId storeOwnerId: Long,
        @RequestBody storeOwnerUpdateDto: StoreOwnerUpdateDto
    ): ResponseEntity<Unit> {
        storeOwnerService.update(storeOwnerId, storeOwnerUpdateDto)
        return ResponseEntity.noContent().build()
    }
}