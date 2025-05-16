package com.eatngo.product.mapper

import com.eatngo.product.domain.*
import com.eatngo.product.domain.Product.*
import com.eatngo.product.entity.InventoryEmbeddable
import com.eatngo.product.entity.ProductEntity
import com.eatngo.product.entity.ProductPriceEmbeddable
import com.eatngo.product.entity.ProductType.*

class ProductMapper {

    companion object {
        fun toEntity(product: Product): ProductEntity {
            val productType = when (product) {
                is LargeLuckBag -> L
                is MediumLuckBag -> M
                is SmallLuckBag -> S
                else -> throw IllegalArgumentException("Unknown product type: $product")
            }

            return ProductEntity(
                id = product.id,
                name = product.name,
                description = product.description,
                inventory = InventoryEmbeddable(
                    product.inventory.quantity,
                    product.inventory.stock
                ),
                price = ProductPriceEmbeddable(
                    product.price.originalPrice,
                    product.price.discountRate,
                    product.price.finalPrice
                ),
                imageUrl = product.imageUrl,
                storeId = product.storeId,
                foodTypes = product.foodTypes.foods.map { it.name },
                status = product.status,
                productType = productType
            )
        }

        fun toDomain(entity: ProductEntity): Product {
            val productId = entity.id
            val createdAt = entity.createdAt
            val updatedAt = entity.updatedAt

            val inventory = Inventory(entity.inventory.quantity, entity.inventory.stock)
            val price = ProductPrice(entity.price.originalPrice, entity.price.discountRate, entity.price.finalPrice)
            val foodTypes = FoodTypes(entity.foodTypes.map { name -> Food(name) })

            return when (entity.productType) {
                L -> LargeLuckBag(
                    id = productId,
                    name = entity.name,
                    description = entity.description,
                    inventory = inventory,
                    price = price,
                    imageUrl = entity.imageUrl,
                    storeId = entity.storeId,
                    foodTypes = foodTypes,
                    status = entity.status,
                    createdAt = createdAt,
                    updatedAt = updatedAt
                )

                M -> MediumLuckBag(
                    id = productId,
                    name = entity.name,
                    description = entity.description,
                    inventory = inventory,
                    price = price,
                    imageUrl = entity.imageUrl,
                    storeId = entity.storeId,
                    foodTypes = foodTypes,
                    status = entity.status,
                    createdAt = createdAt,
                    updatedAt = updatedAt
                )

                S -> SmallLuckBag(
                    id = productId,
                    name = entity.name,
                    description = entity.description,
                    inventory = inventory,
                    price = price,
                    imageUrl = entity.imageUrl,
                    storeId = entity.storeId,
                    foodTypes = foodTypes,
                    status = entity.status,
                    createdAt = createdAt,
                    updatedAt = updatedAt
                )
            }
        }
    }
}