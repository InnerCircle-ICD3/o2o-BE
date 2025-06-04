package com.eatngo.review.usecase

import com.eatngo.customer.service.CustomerService
import com.eatngo.order.service.OrderService
import com.eatngo.review.dto.CreateReviewDto
import com.eatngo.review.dto.ReviewDto
import com.eatngo.review.service.ReviewService
import org.springframework.stereotype.Service

@Service
class ReviewUseCase(
    private val reviewService: ReviewService,
    private val orderService: OrderService,
    private val customerService: CustomerService,
) {

    fun createReview(dto: CreateReviewDto): ReviewDto {
        val customer = customerService.getCustomerById(dto.customerId)
        val order = orderService.getById(dto.orderId)
        return ReviewDto.from(order.createReview(dto = dto, customer = customer))
    }
}