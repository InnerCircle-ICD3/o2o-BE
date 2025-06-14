package com.eatngo.review

import com.eatngo.auth.annotation.CustomerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.review.dto.CreateReviewRequestDto
import com.eatngo.review.dto.ReviewResponseDto
import com.eatngo.review.usecase.CreateReviewUseCase
import com.eatngo.review.usecase.DeleteReviewUseCase
import com.eatngo.review.usecase.ReadReviewUseCase
import com.eatngo.review.usecase.ReadReviewsUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "리뷰", description = "리뷰 관련 API")
@RestController
class ReviewController(
    private val createReviewUseCase: CreateReviewUseCase,
    private val readReviewsUseCase: ReadReviewsUseCase,
    private val readReviewUseCase: ReadReviewUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase,
) {
    @PostMapping("/api/v1/orders/{orderId}/reviews")
    @Operation(summary = "리뷰 생성", description = "리뷰 생성")
    fun createReview(
        @RequestBody requestDto: CreateReviewRequestDto,
        @PathVariable orderId: Long,
        @CustomerId customerId: Long,
    ) = ResponseEntity.ok(
        ApiResponse.success(
            ReviewResponseDto.from(
                createReviewUseCase.createReview(
                    CreateReviewRequestDto.toCreateReviewDto(
                        dto = requestDto,
                        orderId = orderId,
                        customerId = customerId,
                    ),
                ),
            ),
        ),
    )

    @GetMapping("/api/v1/orders/{orderId}/reviews")
    @Operation(summary = "리뷰 조회", description = "리뷰 조회")
    fun getReview(
        @PathVariable orderId: Long,
        @CustomerId customerId: Long,
    ) = ResponseEntity.ok(
        ApiResponse.success(
            ReviewResponseDto.from(
                readReviewUseCase.getReviewByOrderId(orderId = orderId),
            ),
        ),
    )

    @GetMapping("/api/v1/stores/{storeId}/reviews")
    @Operation(summary = "상점 리뷰 목록 조회", description = "상점 리뷰 목록 조회")
    fun getStoreReviews(
        @PathVariable storeId: Long,
        @CustomerId customerId: Long,
        @RequestParam(required = false) lastId: Long?,
    ) = ResponseEntity.ok(
        ApiResponse.success(
            readReviewsUseCase.execute(
                storeId = storeId,
                lastId = lastId,
            ),
        ),
    )

    @DeleteMapping("/api/v1/reviews/{reviewId}")
    @Operation(summary = "작성한 리뷰 삭제", description = "작성한 리뷰 삭제")
    fun deleteReview(
        @PathVariable reviewId: Long,
        @CustomerId customerId: Long,
    ) = ResponseEntity.ok(
        ApiResponse.success(
            deleteReviewUseCase.execute(reviewId = reviewId, customerId = customerId),
        ),
    )
}
