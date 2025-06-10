package com.eatngo.order.usecase

import com.eatngo.common.response.Cursor
import com.eatngo.customer.service.CustomerService
import com.eatngo.order.domain.Status
import com.eatngo.order.dto.OrderListDto
import com.eatngo.order.dto.OrderQueryParamDto
import com.eatngo.order.service.OrderService
import com.eatngo.review.service.ReviewService
import com.eatngo.store.service.StoreService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomerReadOrderUseCase(
    private val orderService: OrderService,
    private val reviewService: ReviewService,
    private val customerService: CustomerService,
    private val storeService: StoreService,
) {
    @Transactional(readOnly = true)
    fun findAllByQueryParameter(queryParam: OrderQueryParamDto): Cursor<OrderListDto> {
        customerService.getCustomerById(queryParam.customerId!!)
        val cursoredOrders = orderService.findAllByQueryParam(queryParam)

        val orderIdToReview =
            reviewService
                .findByOrderIds(
                    cursoredOrders.contents
                        .filter { it.status == Status.DONE }
                        .map { it.id },
                ).associateBy { it.orderId }

        val storeIdToStore =
            storeService.getStoresByIds(cursoredOrders.contents.map { it.storeId }).associateBy { it.id }

        return Cursor.from(
            content =
                cursoredOrders.contents
                    .map {
                        OrderListDto.from(
                            order = it,
                            hasReview = orderIdToReview.containsKey(it.id),
                            storeName = storeIdToStore[it.storeId]!!.name.toString(),
                        )
                    },
            lastId = cursoredOrders.lastId,
        )
    }
}
