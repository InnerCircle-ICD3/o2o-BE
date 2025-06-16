package com.eatngo.review.usecase

import com.eatngo.customer.service.CustomerService
import com.eatngo.review.domain.Review
import com.eatngo.review.service.ReviewService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteReviewUseCase(
    private val reviewService: ReviewService,
    private val customerService: CustomerService,
) {
    @Transactional
    fun execute(
        reviewId: Long,
        customerId: Long,
    ) {
        val customer = customerService.getCustomerById(customerId)
        val review: Review = reviewService.findById(reviewId)

        require(review.canEditable(customer)) { "리뷰를 작성한 사람만이 삭제할 수 있습니다." }

        reviewService.deleteById(reviewId)
    }
}
