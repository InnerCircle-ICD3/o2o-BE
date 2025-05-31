package com.eatngo.inventory.persistence

import com.eatngo.inventory.domain.Inventory
import com.eatngo.inventory.infra.InventoryPersistence
import com.eatngo.inventory.mapper.InventoryMapper
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
            .map(InventoryMapper::toDomain)
            .orElse(null)
    }

    override fun deleteByProductId(productId: Long) {
        jpaInventoryRepository.deleteByProductId(productId)
    }

    override fun updateStock(productId: Long, stockQuantity: Int): Int {
        return jpaInventoryRepository.updateStock(productId, stockQuantity)
    }
}