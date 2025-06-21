package com.eatngo.inventory.persistence

import com.eatngo.inventory.domain.Inventory
import com.eatngo.inventory.infra.InventoryPersistence
import com.eatngo.inventory.mapper.InventoryMapper
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class InventoryRepositoryImpl(
    private val jpaInventoryRepository: JpaInventoryRepository
) : InventoryPersistence {
    override fun save(inventory: Inventory): Inventory {
        return InventoryMapper.toDomain(
            jpaInventoryRepository.save(InventoryMapper.toEntity(inventory))
        )
    }

    override fun findTopByProductIdOrderByVersionDesc(productId: Long, localDate: LocalDate): Inventory? {
        return jpaInventoryRepository.findTopByProductIdAndInventoryDateOrderByVersionDescIdDesc(productId, localDate)
            .map(InventoryMapper::toDomain)
            .orElse(null)
    }

    override fun deleteByProductId(productId: Long) {
        jpaInventoryRepository.deleteByProductId(productId)
    }

    override fun updateStock(productId: Long, stockQuantity: Int, localDate: LocalDate): Int {
        return jpaInventoryRepository.updateStock(productId, stockQuantity, localDate)
    }

    override fun findAllByProductIdIn(productIds: List<Long>, localDate: LocalDate): List<Inventory> {
        return jpaInventoryRepository.findAllByProductIdInAndInventoryDate(productIds, localDate)
            .map(InventoryMapper.Companion::toDomain)
    }

    override fun saveAll(inventories: List<Inventory>) {
        jpaInventoryRepository.saveAll(
            inventories.map(InventoryMapper.Companion::toEntity)
        )
    }

    override fun findLatestByProductIds(productIds: List<Long>): List<Inventory> {
        return jpaInventoryRepository.findLatestByProductIds(productIds)
            .map(InventoryMapper::toDomain)
    }

}