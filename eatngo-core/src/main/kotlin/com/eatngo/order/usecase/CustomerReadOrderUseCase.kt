package com.eatngo.order.usecase

import com.eatngo.common.response.Cursor
import com.eatngo.customer.service.CustomerService
import com.eatngo.order.domain.Status
import com.eatngo.order.dto.OrderDto
import com.eatngo.order.dto.OrderQueryParamDto
import com.eatngo.order.service.OrderService
import com.eatngo.review.service.ReviewService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomerReadOrderUseCase(
    private val orderService: OrderService,
    private val reviewService: ReviewService,
    private val customerService: CustomerService
) {
    @Transactional(readOnly = true)
    fun findAllByQueryParameter(queryParam: OrderQueryParamDto): Cursor<OrderDto> {
        customerService.getCustomerById(queryParam.customerId!!)
        val cursoredOrders = orderService.findAllByQueryParam(queryParam)

        val orderIdToReview =
            reviewService.findByOrderIds(
                cursoredOrders.contents
                    .filter { it.status == Status.DONE }
                    .map { it.id }
            ).associateBy { it.orderId }

        return Cursor.from(
            content = cursoredOrders.contents
                .map { OrderDto.from(it, orderIdToReview.containsKey(it.id)) },
            lastId = cursoredOrders.lastId
        )
    }
}