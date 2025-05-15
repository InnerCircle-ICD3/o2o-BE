package com.eatngo.product.persistence

import com.eatngo.product.infra.ProductPersistence
import org.springframework.stereotype.Repository

@Repository
class ProductPersistenceImpl(
    private val productRepository: JpaProductRepository
) : ProductPersistence {
}