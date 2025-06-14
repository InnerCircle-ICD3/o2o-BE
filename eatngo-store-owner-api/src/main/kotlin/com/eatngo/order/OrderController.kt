package com.eatngo.order

import com.eatngo.auth.annotaion.StoreOwnerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.order.domain.Status
import com.eatngo.order.dto.CursoredStoreOrderQueryParamDto
import com.eatngo.order.dto.StoreOrderStatusChangedDto
import com.eatngo.order.usecase.StoreOrderStatusChangedUseCase
import com.eatngo.order.usecase.StoreReadOrderUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "주문", description = "주문 관련 API")
@RestController
class OrderController(
    private val orderStatusChangedUseCase: StoreOrderStatusChangedUseCase,
    private val storeReadOrderUseCase: StoreReadOrderUseCase,
) {
    @PostMapping("/api/v1/stores/{storeId}/orders/{orderId}/cancel")
    @Operation(summary = "주문 취소", description = "주문 취소")
    fun cancelOrder(
        @StoreOwnerId storeOwnerId: Long,
        @PathVariable orderId: Long,
        @PathVariable storeId: Long,
    ) = ResponseEntity.ok(
        ApiResponse.success(
            orderStatusChangedUseCase.change(
                StoreOrderStatusChangedDto(
                    orderId = orderId,
                    userId = storeOwnerId,
                    status = Status.CANCELED,
                    storeId = storeId,
                ),
            ),
        ),
    )

    @PostMapping("/api/v1/stores/{storeId}/orders/{orderId}/confirm")
    @Operation(summary = "주문 승인", description = "주문 승인")
    fun confirmOrder(
        @StoreOwnerId storeOwnerId: Long,
        @PathVariable orderId: Long,
        @PathVariable storeId: Long,
    ) = ResponseEntity.ok(
        ApiResponse.success(
            orderStatusChangedUseCase.change(
                StoreOrderStatusChangedDto(
                    orderId = orderId,
                    userId = storeOwnerId,
                    status = Status.CONFIRMED,
                    storeId = storeId,
                ),
            ),
        ),
    )

    @GetMapping("/api/v1/stores/{storeId}/store/orders")
    @Operation(summary = "상점 주문 조회", description = "상점 주문 조회")
    fun getOrdersByStoreId(
        @StoreOwnerId storeOwnerId: Long,
        @PathVariable storeId: Long,
        @ModelAttribute cursoredStoreOrderQueryParamDto: CursoredStoreOrderQueryParamDto,
    ) = ResponseEntity.ok(
        ApiResponse.success(
            storeReadOrderUseCase.findAllByQueryParameter(
                CursoredStoreOrderQueryParamDto.toOrderQueryParamDto(
                    storeId = storeId,
                    storeOwnerId = storeOwnerId,
                    dto = cursoredStoreOrderQueryParamDto,
                ),
            ),
        ),
    )
}
