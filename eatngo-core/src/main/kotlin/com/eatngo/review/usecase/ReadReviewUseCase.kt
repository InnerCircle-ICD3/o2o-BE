package com.eatngo.review.usecase

import com.eatngo.common.exception.review.ReviewException
import com.eatngo.extension.orThrow
import com.eatngo.review.dto.ReviewDto
import com.eatngo.review.service.ReviewService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReadReviewUseCase(
    private val reviewService: ReviewService,
) {
    @Transactional(readOnly = true)
    fun getReviewByOrderId(orderId: Long): ReviewDto {
        val review =
            reviewService
                .findByOrderId(orderId)
                .orThrow {
                    ReviewException.ReviewNotFoundExceptionByOrderId(orderId = orderId)
                }

        return ReviewDto.from(review)
    }
}
