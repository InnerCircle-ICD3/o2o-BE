package com.eatngo.product.persistence

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

    override fun findById(productId: Long): Product? {
        return productRepository.findById(productId)
            .map(ProductMapper::toDomain)
            .orElse(null)
    }

    override fun findAllByStoreId(storeId: Long): List<Product> {
        return productRepository.findAllByStoreId(storeId)
            .map(ProductMapper::toDomain)
    }

    override fun findByIdAndStoreId(productId: Long, storeId: Long): Product? {
        return productRepository.findByIdAndStoreId(productId, storeId)
            .map(ProductMapper::toDomain)
            .orElse(null)
    }
}