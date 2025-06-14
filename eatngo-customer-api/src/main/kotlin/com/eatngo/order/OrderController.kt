package com.eatngo.order

import com.eatngo.auth.annotation.CustomerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.order.domain.Status
import com.eatngo.order.dto.CreateOrderRequestDto
import com.eatngo.order.dto.CursoredCustomerOrderQueryParamDto
import com.eatngo.order.dto.OrderCreateDto
import com.eatngo.order.dto.OrderItemCreateDto
import com.eatngo.order.dto.OrderStatusChangedDto
import com.eatngo.order.usecase.CustomerOrderStatusChangedUseCase
import com.eatngo.order.usecase.CustomerReadOrderUseCase
import com.eatngo.order.usecase.OrderCreateUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "주문", description = "주문 관련 API")
@RestController
class OrderController(
    private val orderCreateUseCase: OrderCreateUseCase,
    private val orderStatusChangedUseCase: CustomerOrderStatusChangedUseCase,
    private val customerReadOrderUseCase: CustomerReadOrderUseCase,
) {
    @PostMapping("/api/v1/orders")
    @Operation(summary = "주문 생성", description = "주문 생성")
    fun createOrder(
        @RequestBody requestDto: CreateOrderRequestDto,
        @CustomerId customerId: Long,
    ) = ResponseEntity.ok(
        ApiResponse.success(
            orderCreateUseCase.create(
                OrderCreateDto(
                    customerId = customerId,
                    storeId = requestDto.storeId,
                    pickupDateTime = requestDto.pickupDateTime,
                    orderItems =
                        requestDto.orderItems.map {
                            OrderItemCreateDto(
                                productId = it.productId,
                                productName = it.productName,
                                quantity = it.quantity,
                                price = it.price,
                            )
                        },
                ),
            ),
        ),
    )

    @PostMapping("/api/v1/orders/{orderId}/cancel")
    @Operation(summary = "주문 취소", description = "주문 취소")
    fun cancelOrder(
        @PathVariable orderId: Long,
        @CustomerId customerId: Long,
    ) = ResponseEntity.ok(
        ApiResponse.success(
            orderStatusChangedUseCase.change(
                OrderStatusChangedDto(orderId, customerId, Status.CANCELED),
            ),
        ),
    )

    @PostMapping("/api/v1/orders/{orderId}/ready")
    @Operation(summary = "주문 예약 대기", description = "주문 예약 대기")
    fun readyOrder(
        @PathVariable orderId: Long,
        @CustomerId customerId: Long,
    ) = ResponseEntity.ok(
        ApiResponse.success(
            orderStatusChangedUseCase.change(
                OrderStatusChangedDto(orderId, customerId, Status.READY),
            ),
        ),
    )

    @PostMapping("/api/v1/orders/{orderId}/done")
    @Operation(summary = "주문 완료", description = "주문 완료")
    fun doneOrder(
        @PathVariable orderId: Long,
        @CustomerId customerId: Long,
    ) = ResponseEntity.ok(
        ApiResponse.success(
            orderStatusChangedUseCase.change(
                OrderStatusChangedDto(orderId, customerId, Status.DONE),
            ),
        ),
    )

    @GetMapping("/api/v1/customers/orders")
    @Operation(summary = "내 주문 조회", description = "내 주문 이력 조회")
    fun getOrdersByCustomerId(
        @CustomerId customerId: Long,
        @ParameterObject @ModelAttribute queryParamDto: CursoredCustomerOrderQueryParamDto,
    ) = ResponseEntity.ok(
        ApiResponse.success(
            customerReadOrderUseCase.findAllByQueryParameter(
                CursoredCustomerOrderQueryParamDto.toOrderQueryParamDto(
                    customerId = customerId,
                    dto = queryParamDto,
                ),
            ),
        ),
    )
}
