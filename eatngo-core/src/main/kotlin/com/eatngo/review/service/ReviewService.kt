package com.eatngo.review.service

import com.eatngo.customer.domain.Customer
import com.eatngo.order.domain.Order
import com.eatngo.review.dto.CreateReviewDto
import com.eatngo.review.infra.ReviewPersistence
import org.springframework.stereotype.Service

@Service
class ReviewService(
    private val reviewPersistence: ReviewPersistence,
) {
    fun createReview(
        dto: CreateReviewDto,
        order: Order,
        customer: Customer,
    ) = reviewPersistence.save(
        order.createReview(
            dto = dto,
            customer = customer,
        ),
    )

    fun existsReviewByOrderId(orderId: Long) = reviewPersistence.existsByOrderId(orderId)

    fun findByOrderId(orderId: Long) = reviewPersistence.findByOrderId(orderId)

    fun findByOrderIds(orderIds: List<Long>) = reviewPersistence.findByOrderIds(orderIds)

    fun findByStoreId(
        storeId: Long,
        lastId: Long?,
    ) = reviewPersistence.findByStoreId(storeId, lastId)
}
