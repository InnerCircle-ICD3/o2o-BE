package com.eatngo.review.persistence

import com.eatngo.review.domain.Review
import com.eatngo.review.infra.ReviewPersistence
import com.eatngo.review.rdb.entity.ReviewJpaEntity
import com.eatngo.review.rdb.repository.ReviewRdbRepository
import org.springframework.stereotype.Component

@Component
class ReviewPersistenceImpl(
    private val reviewRdbRepository: ReviewRdbRepository
) : ReviewPersistence {
    override fun save(review: Review) = ReviewJpaEntity.toDomain(
        reviewRdbRepository.save(
            ReviewJpaEntity.from(review)
        )
    )

    override fun existsByOrderId(orderId: Long) = reviewRdbRepository.findByOrderId(orderId) != null

    override fun findByOrderIds(orderIds: List<Long>) = reviewRdbRepository.findByOrderIds(orderIds)
        .map(ReviewJpaEntity::toDomain)
}