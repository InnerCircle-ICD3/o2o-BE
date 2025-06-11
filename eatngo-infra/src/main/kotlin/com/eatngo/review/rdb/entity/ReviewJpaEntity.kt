package com.eatngo.review.rdb.entity

import com.eatngo.common.BaseJpaEntity
import com.eatngo.constants.DELETED_FILTER
import com.eatngo.review.domain.Images
import com.eatngo.review.domain.Review
import com.eatngo.review.domain.Score
import jakarta.persistence.CollectionTable
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table
import org.hibernate.annotations.Filter

@Filter(name = DELETED_FILTER)
@Table(name = "reviews")
@Entity
class ReviewJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val orderId: Long,
    val content: String,
    val score: Int,
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "review_images", joinColumns = [JoinColumn(name = "review_id")])
    val images: MutableList<String> = mutableListOf(),
    val customerId: Long,
) : BaseJpaEntity() {
    companion object {
        fun from(review: Review): ReviewJpaEntity =
            ReviewJpaEntity(
                orderId = review.orderId,
                content = review.content,
                score = review.score.value,
                images = review.images.images.toMutableList(),
                customerId = review.customerId,
            )

        fun toDomain(reviewJpaEntity: ReviewJpaEntity) =
            with(reviewJpaEntity) {
                Review(
                    id = reviewJpaEntity.id,
                    orderId = reviewJpaEntity.orderId,
                    content = reviewJpaEntity.content,
                    score = Score(reviewJpaEntity.score),
                    images = Images(reviewJpaEntity.images),
                    customerId = reviewJpaEntity.customerId,
                    createdAt = reviewJpaEntity.createdAt,
                    createdBy = reviewJpaEntity.createdBy,
                    updatedAt = reviewJpaEntity.updatedAt,
                    updatedBy = reviewJpaEntity.updatedBy,
                    deletedAt = reviewJpaEntity.deletedAt,
                )
            }
    }
}
