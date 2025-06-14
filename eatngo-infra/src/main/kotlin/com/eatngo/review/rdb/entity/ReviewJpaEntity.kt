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
    val nickname: String,
) : BaseJpaEntity() {
    companion object {
        fun from(review: Review) =
            with(review) {
                ReviewJpaEntity(
                    orderId = orderId,
                    content = content,
                    score = score.value,
                    images = images.images.toMutableList(),
                    customerId = customerId,
                    nickname = nickname,
                )
            }

        fun toDomain(reviewJpaEntity: ReviewJpaEntity) =
            with(reviewJpaEntity) {
                Review(
                    id = id,
                    orderId = orderId,
                    content = content,
                    score = Score(score),
                    images = Images(images),
                    nickname = nickname,
                    customerId = customerId,
                    createdAt = createdAt,
                    createdBy = createdBy,
                    updatedAt = updatedAt,
                    updatedBy = updatedBy,
                    deletedAt = deletedAt,
                )
            }
    }
}
