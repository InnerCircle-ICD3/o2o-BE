package com.eatngo.product.mapper

import com.eatngo.product.domain.*
import com.eatngo.product.domain.Product.*
import com.eatngo.product.entity.ProductEntity
import com.eatngo.product.entity.ProductPriceEmbeddable
import com.eatngo.product.entity.ProductType.*
import org.hibernate.annotations.SoftDeleteType

class ProductMapper {

    companion object {
        fun toEntity(product: Product): ProductEntity {
            val productType = when (product) {
                is LargeEatNGoBag -> L
                is MediumEatNGoBag -> M
                is SmallEatNGoBag -> S
                else -> throw IllegalArgumentException("Unknown product type: $product")
            }

            return ProductEntity(
                id = product.id,
                name = product.name,
                description = product.description,
                price = ProductPriceEmbeddable(
                    product.price.originalPrice,
                    product.price.discountRate,
                    product.price.finalPrice
                ),
                imageUrl = product.imageUrl,
                storeId = product.storeId!!,
                foodTypes = product.foodTypes.foods.map { it.name },
                status = product.status,
                productType = productType,
                deleteStatus = SoftDeleteType.valueOf(product.deletedStatus.value)
            )
        }

        fun toDomain(entity: ProductEntity): Product {
            val productId = entity.id
            val createdAt = entity.createdAt
            val updatedAt = entity.updatedAt

            val price = ProductPrice(entity.price.originalPrice, entity.price.discountRate)
            val foodTypes = FoodTypes(entity.foodTypes.map { Food(it) })

            return when (entity.productType) {
                L -> LargeEatNGoBag(
                    id = productId,
                    name = entity.name,
                    description = entity.description,
                    price = price,
                    imageUrl = entity.imageUrl,
                    storeId = entity.storeId,
                    foodTypes = foodTypes,
                    status = entity.status,
                    deletedStatus = DeletedStatus.valueOf(entity.deleteStatus.name),
                    createdAt = createdAt,
                    updatedAt = updatedAt
                )

                M -> MediumEatNGoBag(
                    id = productId,
                    name = entity.name,
                    description = entity.description,
                    price = price,
                    imageUrl = entity.imageUrl,
                    storeId = entity.storeId,
                    foodTypes = foodTypes,
                    status = entity.status,
                    deletedStatus = DeletedStatus.valueOf(entity.deleteStatus.name),
                    createdAt = createdAt,
                    updatedAt = updatedAt
                )

                S -> SmallEatNGoBag(
                    id = productId,
                    name = entity.name,
                    description = entity.description,
                    price = price,
                    imageUrl = entity.imageUrl,
                    storeId = entity.storeId,
                    foodTypes = foodTypes,
                    status = entity.status,
                    deletedStatus = DeletedStatus.valueOf(entity.deleteStatus.name),
                    createdAt = createdAt,
                    updatedAt = updatedAt
                )
            }
        }
    }
}