package com.eatngo.review.persistence

import com.eatngo.aop.SoftDeletedFilter
import com.eatngo.common.exception.review.ReviewException
import com.eatngo.common.response.Cursor
import com.eatngo.extension.mapOrNull
import com.eatngo.review.domain.Review
import com.eatngo.review.infra.ReviewPersistence
import com.eatngo.review.rdb.entity.ReviewJpaEntity
import com.eatngo.review.rdb.repository.ReviewRdbRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
class ReviewPersistenceImpl(
    private val reviewRdbRepository: ReviewRdbRepository,
) : ReviewPersistence {
    @SoftDeletedFilter
    override fun save(review: Review) =
        ReviewJpaEntity.toDomain(
            reviewRdbRepository.save(
                ReviewJpaEntity.from(review),
            ),
        )

    @SoftDeletedFilter
    override fun existsByOrderId(orderId: Long) = reviewRdbRepository.findByOrderId(orderId) != null

    @SoftDeletedFilter
    override fun findByOrderId(orderId: Long) =
        reviewRdbRepository
            .findByOrderId(orderId)
            ?.let(ReviewJpaEntity::toDomain)

    @SoftDeletedFilter
    override fun findByOrderIds(orderIds: List<Long>) =
        reviewRdbRepository
            .findByOrderIds(orderIds)
            .map(ReviewJpaEntity::toDomain)

    @SoftDeletedFilter
    override fun findByStoreId(
        storeId: Long,
        lastId: Long?,
    ): Cursor<Review> {
        val pageRequest = PageRequest.of(0, 50, Sort.by("id").descending())

        val cursoredReviewJpaEntities =
            reviewRdbRepository.cursoredFindAllByStoreId(
                storeId = storeId,
                lastId = lastId,
                pageable = pageRequest,
            )

        return Cursor.from(
            cursoredReviewJpaEntities.content
                .map(ReviewJpaEntity::toDomain),
            cursoredReviewJpaEntities.lastOrNull()?.id,
        )
    }

    @SoftDeletedFilter
    override fun findById(id: Long): Review? =
        reviewRdbRepository
            .findById(id)
            .mapOrNull(ReviewJpaEntity::toDomain)

    @SoftDeletedFilter
    override fun deleteById(id: Long) {
        reviewRdbRepository
            .findById(id)
            .orElseThrow { ReviewException.ReviewNotFoundException(id) }
            .apply { delete() }
    }
}
