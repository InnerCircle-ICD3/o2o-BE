package com.eatngo.product.entity

import com.eatngo.common.BaseJpaEntity
import com.eatngo.constants.DELETED_FILTER
import com.eatngo.product.domain.DeletedStatus
import com.eatngo.product.domain.ProductStatus
import jakarta.persistence.*
import org.hibernate.annotations.Filter
import org.springframework.data.redis.core.RedisHash

@Filter(name = DELETED_FILTER)
@Entity
@Table(name = "products")
@RedisHash(value = "product", timeToLive = 600)
// TODO 이후 DType 추가 필요!
data class ProductEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    @Column(nullable = false)
    var name: String,

    @Column(columnDefinition = "TEXT")
    var description: String,

    @Embedded
    var price: ProductPriceEmbeddable,

    @Column(columnDefinition = "TEXT")
    var imageUrl: String?,

    @Column(nullable = false)
    val storeId: Long,

    @ElementCollection
    @CollectionTable(name = "product_entity_food_types", joinColumns = [JoinColumn(name = "product_entity_id")])
    @Column(name = "food_type", nullable = false)
    var foodTypes: List<String>,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ProductStatus,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var productType: ProductType,

    @Enumerated(EnumType.STRING)
    var deleteStatus: DeletedStatus,
) : BaseJpaEntity() {
}