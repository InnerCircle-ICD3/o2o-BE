package com.eatngo.product.persistence

import com.eatngo.product.domain.Inventory
import com.eatngo.product.infra.InventoryPersistence
import com.eatngo.product.mapper.InventoryMapper
import org.springframework.stereotype.Repository

@Repository
class InventoryRepositoryImpl(
    private val jpaInventoryRepository: JpaInventoryRepository
) : InventoryPersistence {
    override fun save(inventory: Inventory): Inventory {
        return InventoryMapper.toDomain(
            jpaInventoryRepository.save(InventoryMapper.toEntity(inventory))
        )
    }

    override fun findTopByProductIdOrderByVersionDesc(productId: Long): Inventory? {
        return jpaInventoryRepository.findTopByProductIdOrderByVersionDesc(productId)
    }

    override fun deleteById(productId: Long) {
        jpaInventoryRepository.deleteByProductId(productId)
    }
}