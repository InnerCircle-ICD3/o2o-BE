package com.eatngo.product.service

import com.eatngo.file.FileStorageService
import com.eatngo.product.domain.*
import com.eatngo.product.domain.Product.*
import com.eatngo.product.domain.ProductSizeType.*
import com.eatngo.product.dto.ProductDto
import com.eatngo.product.infra.ProductPersistence
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ProductService(
    private val productPersistence: ProductPersistence,
    private val fileStorageService: FileStorageService
    // TODO storeRepository
) {
    companion object {
        private const val PRODUCT_IMAGE_PATH = "product"
    }

    fun createProduct(
        createProductDto: ProductDto,
        image: MultipartFile
    ): ProductDto {
        // TODO storeRepo.findById()

        val inventory = Inventory.create(createProductDto.inventory.quantity)
        val price = ProductPrice.create(createProductDto.price.originalPrice)
        val foodTypes = FoodTypes.create(createProductDto.foodTypes)
        val imageUrl = fileStorageService.saveFile(image, PRODUCT_IMAGE_PATH)

        val product: Product = when (ProductSizeType.fromValue(createProductDto.size)) {
            L -> {
                LargeLuckBag(
                    id = null,
                    name = createProductDto.name,
                    description = createProductDto.description,
                    inventory = inventory,
                    price = price,
                    imageUrl = imageUrl,
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
                    imageUrl = imageUrl,
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
                    imageUrl = imageUrl,
                    storeId = createProductDto.storeId,
                    foodTypes = foodTypes,
                )
            }
        }

        val savedProduct: Product = productPersistence.save(product)

        return ProductDto.from(savedProduct)
    }
}