package com.eatngo.product.persistence

import com.eatngo.product.domain.DeletedStatus
import com.eatngo.product.domain.Product
import com.eatngo.product.infra.ProductPersistence
import com.eatngo.product.mapper.ProductMapper
import org.springframework.stereotype.Repository

@Repository
class ProductPersistenceImpl(
    private val productRepository: JpaProductRepository
) : ProductPersistence {
    override fun save(product: Product): Product {
        val productEntity = ProductMapper.toEntity(product)
        val savedEntity = productRepository.save(productEntity)
        return ProductMapper.toDomain(savedEntity)
    }

    override fun findActivatedProductById(productId: Long): Product? {
        return productRepository.findByIdAndDeleteStatus(productId, DeletedStatus.ACTIVE)
            .map(ProductMapper::toDomain)
            .orElse(null)
    }

    override fun findAllActivatedProductByStoreId(storeId: Long): List<Product> {
        return productRepository.findAllByStoreIdAndDeleteStatusOrderByCreatedAtDesc(storeId, DeletedStatus.ACTIVE)
            .map(ProductMapper::toDomain)
    }

    override fun findActivatedProductByIdAndStoreId(productId: Long, storeId: Long): Product? {
        return productRepository.findByIdAndStoreIdAndDeleteStatus(productId, storeId, DeletedStatus.ACTIVE)
            .map(ProductMapper::toDomain)
            .orElse(null)
    }

    override fun findAllActivatedProductsByStoreIds(storeIds: List<Long>): List<Product> {
        return productRepository.findAllByStoreIdInAndDeleteStatus(storeIds, DeletedStatus.ACTIVE)
            .map(ProductMapper::toDomain)
    }
}