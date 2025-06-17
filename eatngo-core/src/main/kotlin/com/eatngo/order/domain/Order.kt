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
    val nickname: String,
    val storeId: Long,
    val pickupDateTime: LocalDateTime,
    var status: Status,
    val statusChangedHistories: MutableList<OrderStatusHistory> = mutableListOf(),
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    fun toReady(customer: Customer) {
        customer.isEditable(customerId)
        status = status.ready()
        statusChangedHistories.add(OrderStatusHistory.from(status, customer))
    }

    fun toCancel(customer: Customer) {
        customer.isEditable(customerId)
        status = status.cancel()
        statusChangedHistories.add(OrderStatusHistory.from(status, customer))
    }

    fun toCancel(store: Store) {
        store.isEditable(storeId)
        status = status.cancel()
        statusChangedHistories.add(OrderStatusHistory.from(status, store))
    }

    fun toCancelByTimeout(now: LocalDateTime) {
        require(
            now.isAfter(
                updatedAt
                    .plusMinutes(AUTO_ORDER_CANCELED_MINUTE),
            ),
        ) { "is not old" }
        status = status.cancel()
        statusChangedHistories.add(OrderStatusHistory.from(status, customerId))
    }

    fun toConfirm(store: Store) {
        store.isEditable(storeId)
        status = status.confirm()
        statusChangedHistories.add(OrderStatusHistory.from(status, store))
    }

    fun toDone(customer: Customer) {
        customer.isEditable(customerId)
        status = status.done()
        statusChangedHistories.add(OrderStatusHistory.from(status, customer))
    }

    fun createReview(
        dto: CreateReviewDto,
        customer: Customer,
    ): Review {
        require(customerId == customer.id) { "주문한 손님만이 리뷰를 작성할 수 있습니다." }
        require(status.equals(Status.DONE)) { "완료된 주문에만 리뷰를 작성할 수 있습니다." }

        return Review.create(
            content = dto.content,
            score = Score(dto.score),
            images = Images(dto.images),
            orderId = id,
            customerId = customer.id,
            nickname = customer.account.nickname?.toString() ?: "",
        )
    }

    private fun Customer.isEditable(customerId: Long) {
        require(customerId == this.id) { "Customer can't edit this order" }
    }

    private fun Store.isEditable(storeId: Long) {
        require(storeId == this.id) { "Store can't edit this order" }
    }

    companion object {
        const val AUTO_ORDER_CANCELED_MINUTE: Long = 10

        fun create(
            customerId: Long,
            storeId: Long,
            orderNumber: Long,
            nickname: String,
            pickupDateTime: LocalDateTime,
            orderItems: List<OrderItem>,
        ): Order =
            Order(
                id = 0,
                orderNumber = orderNumber,
                orderItems = orderItems,
                customerId = customerId,
                nickname = nickname,
                storeId = storeId,
                status = Status.CREATED,
                pickupDateTime = pickupDateTime,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
            )
    }
}
