package com.eatngo.product.persistence

import com.eatngo.product.infra.InventoryPersistence

class InventoryRepositoryImpl(
    private val jpaInventoryRepository: JpaInventoryRepository
) : InventoryPersistence {
}