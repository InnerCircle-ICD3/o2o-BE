package com.eatngo.review

import com.eatngo.auth.annotaion.StoreOwnerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.review.usecase.ReadReviewsUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "리뷰", description = "리뷰 관련 API")
@RestController
class ReviewController(
    private val readReviewsUseCase: ReadReviewsUseCase
) {
    @GetMapping("/api/v1/stores/{storeId}/reviews")
    @Operation(summary = "상점 리뷰 목록 조회", description = "상점 리뷰 목록 조회")
    fun getStoreReviews(
        @PathVariable storeId: Long,
        @StoreOwnerId storeOwnerId: StoreOwnerId,
        @RequestParam(required = false) lastId: Long?
    ) = ResponseEntity.ok(
        ApiResponse.success(
            readReviewsUseCase.execute(
                storeId = storeId,
                lastId = lastId,
            )
        )
    )
}