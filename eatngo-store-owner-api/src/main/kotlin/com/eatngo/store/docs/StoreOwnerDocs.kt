package com.eatngo.store.docs

import com.eatngo.auth.annotaion.StoreOwnerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.store.dto.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(name = "매장 관리", description = "점주의 매장 관리 API")
interface StoreOwnerDocs {

    @Operation(
        summary = "매장 목록 조회",
        description = "점주가 운영 중인 매장 목록을 조회합니다."
    )
    fun getStoresByOwnerId(
        @Parameter(description = "점주 ID", required = true)
        @StoreOwnerId storeOwnerId: Long
    ): ApiResponse<List<StoreDetailResponse>>

    @Operation(
        summary = "매장 등록",
        description = "새로운 매장을 등록합니다. 매장 등록 시 필수 정보와 선택 정보를 입력받습니다."
    )
    fun createStore(
        @Parameter(description = "매장 생성 정보", required = true)
        @RequestBody request: StoreCreateRequest,
        
        @Parameter(description = "점주 ID", required = true)
        @StoreOwnerId storeOwnerId: Long
    ): ApiResponse<StoreCUDResponse>

    @Operation(
        summary = "매장 정보 수정",
        description = "등록된 매장의 정보를 수정합니다. 수정 가능한 모든 필드는 선택적으로 입력 가능합니다."
    )
    fun updateStore(
        @Parameter(description = "수정할 매장 ID", required = true)
        @PathVariable storeId: Long,
        
        @Parameter(description = "매장 수정 정보", required = true)
        @RequestBody request: StoreUpdateRequest,
        
        @Parameter(description = "점주 ID", required = true)
        @StoreOwnerId storeOwnerId: Long
    ): ApiResponse<StoreCUDResponse>

    @Operation(
        summary = "매장 상태 변경",
        description = "매장의 영업 상태를 변경합니다. (OPEN: 영업중, CLOSED: 영업종료, PENDING: 승인대기)"
    )
    fun updateStoreOnlyStatus(
        @Parameter(description = "상태 변경할 매장 ID", required = true)
        @PathVariable storeId: Long,
        
        @Parameter(description = "변경할 매장 상태", required = true)
        @RequestBody request: StoreStatusUpdateRequest,
        
        @Parameter(description = "점주 ID", required = true)
        @StoreOwnerId storeOwnerId: Long
    ): ApiResponse<StoreCUDResponse>

    @Operation(
        summary = "매장 삭제",
        description = "등록된 매장을 삭제합니다. 삭제된 매장은 복구할 수 없습니다."
    )
    fun deleteStore(
        @Parameter(description = "삭제할 매장 ID", required = true)
        @PathVariable storeId: Long,
        
        @Parameter(description = "점주 ID", required = true)
        @StoreOwnerId storeOwnerId: Long
    ): ApiResponse<StoreCUDResponse>
}

@Schema(description = "매장 상태 변경 요청")
data class StoreStatusUpdateRequestDocs(
    @field:Schema(
        description = "변경할 매장 상태",
        example = "OPEN",
        allowableValues = ["OPEN", "CLOSED", "PENDING"],
        required = true
    )
    val status: String
) 