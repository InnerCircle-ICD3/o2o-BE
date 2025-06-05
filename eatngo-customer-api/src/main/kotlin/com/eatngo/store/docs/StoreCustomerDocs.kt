package com.eatngo.store.docs

import com.eatngo.common.response.ApiResponse
import com.eatngo.store.dto.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(name = "매장 API", description = "매장 관련 API")
interface StoreCustomerDocs {

    @Operation(
        summary = "매장 상세 조회",
        description = "매장의 상세 정보를 조회합니다."
    )
    fun getStoreById(
        @Parameter(description = "조회할 매장 ID", required = true)
        @PathVariable storeId: Long
    ): ApiResponse<StoreDetailResponse>
}