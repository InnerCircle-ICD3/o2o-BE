package com.eatngo.review.usecase

import com.eatngo.common.response.Cursor
import com.eatngo.review.dto.ReviewDto
import com.eatngo.review.service.ReviewService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReadReviewsUseCase(
    private val reviewService: ReviewService,
) {
    @Transactional(readOnly = true)
    fun execute(
        storeId: Long,
        lastId: Long?,
    ): Cursor<ReviewDto> {
        val reviews = reviewService.findByStoreId(storeId, lastId)
        return Cursor.from(
            content =
                reviews.contents
                    .map(ReviewDto::from),
            lastId =
                reviews.contents
                    .lastOrNull()
                    ?.id,
        )
    }
}
