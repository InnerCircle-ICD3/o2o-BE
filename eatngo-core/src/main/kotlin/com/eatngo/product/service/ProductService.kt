package com.eatngo.product.service

import com.eatngo.file.FileStorageService
import com.eatngo.product.domain.*
import com.eatngo.product.domain.Product.*
import com.eatngo.product.domain.ProductSizeType.*
import com.eatngo.product.dto.ProductDto
import com.eatngo.product.infra.ProductPersistence
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productPersistence: ProductPersistence,
    private val fileStorageService: FileStorageService
    // TODO storeRepository
) {
    fun createProduct(
        productDto: ProductDto,
    ): ProductDto {
        // TODO storeRepo.findById()

        val inventory = Inventory.create(productDto.inventory.quantity)
        val price = ProductPrice.create(productDto.price.originalPrice)
        val foodTypes = FoodTypes.create(productDto.foodTypes)

        val product: Product = when (ProductSizeType.fromValue(productDto.size)) {
            L -> {
                LargeEatNGoBag(
                    name = productDto.name,
                    description = productDto.description,
                    inventory = inventory,
                    price = price,
                    imageUrl = productDto.imageUrl,
                    storeId = productDto.storeId,
                    foodTypes = foodTypes,
                )
            }

            M -> {
                MediumEatNGoBag(
                    name = productDto.name,
                    description = productDto.description,
                    inventory = inventory,
                    price = price,
                    imageUrl = productDto.imageUrl,
                    storeId = productDto.storeId,
                    foodTypes = foodTypes,
                )
            }

            S -> {
                SmallEatNGoBag(
                    name = productDto.name,
                    description = productDto.description,
                    inventory = inventory,
                    price = price,
                    imageUrl = productDto.imageUrl,
                    storeId = productDto.storeId,
                    foodTypes = foodTypes,
                )
            }
        }

        val savedProduct: Product = productPersistence.save(product)

        return ProductDto.from(
            savedProduct,
            productDto.imageUrl?.let { fileStorageService.resolveImageUrl(it) }
        )
    }

    fun getProductDetails(
        storeId: Long,
        productId: Long
    ): ProductDto {
        // TODO storePersistence.findById(storeId)
        val product: Product = (productPersistence.findById(productId)
            ?: throw IllegalArgumentException("상품을 찾을 수 없습니다."))

        return ProductDto.from(
            product,
            product.imageUrl?.let { fileStorageService.resolveImageUrl(it) }
        )
    }

    fun findAllProducts(storeId: Long): List<ProductDto> = productPersistence.findAllByStoreId(storeId)
        .map { it ->
            ProductDto.from(
                it,
                it.imageUrl?.let { fileStorageService.resolveImageUrl(it) }
            )
        }
}