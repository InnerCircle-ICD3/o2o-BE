package com.eatngo.product.entity

import com.eatngo.product.domain.ProductStatus
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.SoftDelete
import org.hibernate.annotations.SoftDeleteType
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "products")
@SoftDelete
// TODO 이후 DType 추가 필요!
data class ProductEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(columnDefinition = "TEXT")
    var description: String,

    @Embedded
    var inventory: InventoryEmbeddable,

    @Embedded
    var price: ProductPriceEmbeddable,

    @Column(columnDefinition = "TEXT")
    var imageUrl: String?,

    @Column(nullable = false)
    var storeId: Long,

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
    var deleteStatus: SoftDeleteType,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    @Column(nullable = false)
    var updatedAt: LocalDateTime? = LocalDateTime.now()
) {
}