package com.eatngo.review

import com.eatngo.auth.annotation.CustomerId
import com.eatngo.review.dto.CreateReviewRequestDto
import com.eatngo.review.dto.ReviewDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "리뷰", description = "리뷰 관련 API")
@RestController
class ReviewController {
    @PostMapping("/api/v1/orders/{orderId}/reviews")
    @Operation(summary = "리뷰 생성", description = "리뷰 생성")
    fun createReview(
        @RequestBody requestDto: CreateReviewRequestDto,
        @PathVariable orderId: String,
        @CustomerId customerId: Long
    ): ResponseEntity<ReviewDto> {
        return ResponseEntity.ok(
            ReviewDto(
                id = 1L,
                content = "잘먹었습니다.",
                score = 5
            )
        )
    }
}