package com.eatngo.review

import com.eatngo.auth.annotation.CustomerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.review.dto.CreateReviewRequestDto
import com.eatngo.review.dto.ReviewResponseDto
import com.eatngo.review.usecase.ReviewUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "리뷰", description = "리뷰 관련 API")
@RestController
class ReviewController(
    private val reviewUseCase: ReviewUseCase
) {
    @PostMapping("/api/v1/orders/{orderId}/reviews")
    @Operation(summary = "리뷰 생성", description = "리뷰 생성")
    fun createReview(
        @RequestBody requestDto: CreateReviewRequestDto,
        @PathVariable orderId: Long,
        @CustomerId customerId: Long
    ) = ResponseEntity.ok(
        ApiResponse.success(
            ReviewResponseDto.from(
                reviewUseCase.createReview(
                    CreateReviewRequestDto.toCreateReviewDto(
                        dto = requestDto,
                        orderId = orderId,
                        customerId = customerId
                    )
                )
            )
        )
    )
}