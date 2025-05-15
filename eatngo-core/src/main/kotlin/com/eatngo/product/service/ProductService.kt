package com.eatngo.product.service

import com.eatngo.product.domain.*
import com.eatngo.product.domain.Product.*
import com.eatngo.product.domain.ProductSizeType.*
import com.eatngo.product.dto.ProductCreateDto
import com.eatngo.product.infra.ProductPersistence
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productPersistence: ProductPersistence,
    // TODO storeRepository
) {

    fun createProduct(createProductDto: ProductCreateDto): Unit {
        // TODO storeRepo.findById()

        val inventory = Inventory.create(createProductDto.inventory.quantity)
        val price = ProductPrice.create(createProductDto.price.originalPrice)
        val foodTypes = FoodTypes.create(createProductDto.foodTypes)

        val product: Product = when (ProductSizeType.fromValue(createProductDto.size)) {
            L -> {
                LargeLuckBag(
                    id = null,
                    name = createProductDto.name,
                    description = createProductDto.description,
                    inventory = inventory,
                    price = price,
                    imageUrl = createProductDto.imageUrl,
                    storeId = createProductDto.storeId,
                    foodTypes = foodTypes,
                )
            }
            M -> {
                MediumLuckBag(
                    id = null,
                    name = createProductDto.name,
                    description = createProductDto.description,
                    inventory = inventory,
                    price = price,
                    imageUrl = createProductDto.imageUrl,
                    storeId = createProductDto.storeId,
                    foodTypes = foodTypes,
                )
            }
            S -> {
                SmallLuckBag(
                    id = null,
                    name = createProductDto.name,
                    description = createProductDto.description,
                    inventory = inventory,
                    price = price,
                    imageUrl = createProductDto.imageUrl,
                    storeId = createProductDto.storeId,
                    foodTypes = foodTypes,
                )
            }
            else -> throw IllegalArgumentException("Invalid product size: ${createProductDto.size}")
        }

        val savedProduct: Product = productPersistence.save(product)

        // TODO response needed
    }
}