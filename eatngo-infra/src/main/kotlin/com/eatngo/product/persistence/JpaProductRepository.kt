package com.eatngo.product.persistence

import com.eatngo.product.domain.DeletedStatus
import com.eatngo.product.entity.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface JpaProductRepository : JpaRepository<ProductEntity, Long> {
    fun findByIdAndDeleteStatus(productId: Long, deleteStatus: DeletedStatus): Optional<ProductEntity>
    fun findAllByStoreIdAndDeleteStatusOrderByCreatedAtDesc(storeId: Long, deleteStatus: DeletedStatus): List<ProductEntity>
    fun findAllByStoreIdAndDeleteStatus(storeId: Long, deleteStatus: DeletedStatus): List<ProductEntity>
    fun findAllByStoreIdInAndDeleteStatus(storeIds: List<Long>, deleteStatus: DeletedStatus): List<ProductEntity>
    fun findByIdAndStoreIdAndDeleteStatus(
        productId: Long,
        storeId: Long,
        deleteStatus: DeletedStatus
    ): Optional<ProductEntity>
}
