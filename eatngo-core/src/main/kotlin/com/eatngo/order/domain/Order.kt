package com.eatngo.order.domain

import com.eatngo.customer.domain.Customer
import com.eatngo.review.domain.Images
import com.eatngo.review.domain.Review
import com.eatngo.review.domain.Score
import com.eatngo.review.dto.CreateReviewDto
import com.eatngo.store.domain.Store
import java.time.LocalDateTime

class Order(
    val id: Long = 0,
    val orderNumber: Long,
    val orderItems: List<OrderItem>,
    val customerId: Long,
    val storeId: Long,
    val pickupDateTime: LocalDateTime,
    var status: Status,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {

    fun toCancel(customer: Customer) {
        customer.isEditable(customerId)
        status = status.cancel()
    }

    fun toCancel(store: Store) {
        store.isEditable(storeId)
        status = status.cancel()
    }

    fun toConfirm(store: Store) {
        store.isEditable(storeId)
        status = status.confirm()
    }

    fun toDone(customer: Customer) {
        customer.isEditable(customerId)
        status = status.done()
    }

    fun createReview(dto: CreateReviewDto, customer: Customer): Review {
        require(customerId == customer.id) { "주문한 손님만이 리뷰를 작성할 수 있습니다." }
        require(status.equals(Status.DONE)) { "완료된 주문에만 리뷰를 작성할 수 있습니다." }

        return Review.create(
            content = dto.content,
            score = Score(dto.score),
            images = Images(dto.images),
            orderId = id,
            customerId = customer.id
        )
    }

    private fun Customer.isEditable(customerId: Long) {
        require(customerId == this.id) { "Customer can't edit this order" }
    }

    private fun Store.isEditable(storeId: Long) {
        require(storeId == this.id) { "Store can't edit this order" }
    }


    companion object {
        fun create(
            customerId: Long,
            storeId: Long,
            orderNumber: Long,
            pickupDateTime: LocalDateTime,
            orderItems: List<OrderItem>
        ): Order {
            return Order(
                id = 0,
                orderNumber = orderNumber,
                orderItems = orderItems,
                customerId = customerId,
                storeId = storeId,
                status = Status.CREATED,
                pickupDateTime = pickupDateTime,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        }
    }
}