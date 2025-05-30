package com.eatngo.product.service

import com.eatngo.common.exception.ProductException.ProductNotFound
import com.eatngo.extension.orThrow
import com.eatngo.file.FileStorageService
import com.eatngo.inventory.dto.InventoryDto
import com.eatngo.inventory.service.InventoryService
import com.eatngo.product.domain.*
import com.eatngo.product.domain.Product.*
import com.eatngo.product.domain.ProductSizeType.*
import com.eatngo.product.dto.ProductAfterStockDto
import com.eatngo.product.dto.ProductCurrentStockDto
import com.eatngo.product.dto.ProductDto
import com.eatngo.product.infra.ProductPersistence
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductService(
    private val productPersistence: ProductPersistence,
    private val fileStorageService: FileStorageService,
    private val inventoryService: InventoryService,
    // TODO storeRepository
) {

    @CachePut("product", key = "#result.id")
    fun createProduct(
        productDto: ProductDto,
    ): ProductDto {
        // TODO storeRepo.findById()
        val price = ProductPrice.create(productDto.price.originalPrice)
        val foodTypes = FoodTypes.create(productDto.foodTypes)

        val product: Product = when (ProductSizeType.fromValue(productDto.size)) {
            L -> {
                LargeEatNGoBag(
                    name = productDto.name,
                    description = productDto.description,
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
                    price = price,
                    imageUrl = productDto.imageUrl,
                    storeId = productDto.storeId,
                    foodTypes = foodTypes,
                )
            }
        }

        val savedProduct: Product = productPersistence.save(product)
        val savedInventory: InventoryDto = inventoryService.createInventory(productDto)

        return ProductDto.from(
            savedProduct,
            savedProduct.imageUrl?.let(fileStorageService::resolveImageUrl),
            savedInventory
        )
    }

    @Cacheable("product", key = "#productId")
    fun getProductDetails(
        storeId: Long,
        productId: Long
    ): ProductDto {
        // TODO storePersistence.findById(storeId)
        val product: Product = productPersistence.findById(productId)
            .orThrow { ProductNotFound(productId) }

        val inventoryDetails: InventoryDto = inventoryService.getInventoryDetails(productId)

        return ProductDto.from(
            product,
            product.imageUrl?.let(fileStorageService::resolveImageUrl),
            inventoryDetails
        )
    }

    @Cacheable("storeProducts", key = "#storeId")
    fun findAllProducts(storeId: Long): List<ProductDto> {
        var products: List<Product> = productPersistence.findAllByStoreId(storeId)
        if (products.isEmpty()) {
            products = productPersistence.findAllByStoreId(storeId)
        }

        return products
            .map {
                ProductDto.from(
                    it,
                    it.imageUrl?.let(fileStorageService::resolveImageUrl),
                    inventoryService.getInventoryDetails(it.id)
                )
            }
    }

    @CacheEvict("product", key = "#productId")
    fun deleteProduct(storeId: Long, productId: Long) {
        val product: Product = productPersistence.findById(productId).orThrow { ProductNotFound(productId) }
        product.remove()
        productPersistence.save(product)
        inventoryService.deleteInventory(product.id)
    }

    @CacheEvict("product", key = "#productCurrentStockDto.id")
    fun toggleStock(productCurrentStockDto: ProductCurrentStockDto): ProductAfterStockDto {
        val product: Product = productPersistence.findById(productCurrentStockDto.id)
            .orThrow { ProductNotFound(productCurrentStockDto.id) }
        val savedProduct = productPersistence.save(product)

        val changedInventory: InventoryDto = inventoryService.toggleInventory(productCurrentStockDto)

        return ProductAfterStockDto.create(savedProduct, changedInventory)
    }

    @Caching(
        put = [CachePut("product", key = "#productDto.id")],
        evict = [CacheEvict("storeProducts", key = "#productDto.storeId")]
    )
    fun modifyProduct(productDto: ProductDto): ProductDto {
        val product: Product = productPersistence.findByIdAndStoreId(productDto.id!!, productDto.storeId)
            .orThrow { ProductNotFound(productDto.id!!) }

        product.modify(
            name = productDto.name,
            description = productDto.description,
            price = ProductPrice(
                productDto.price.originalPrice,
                productDto.price.discountRate
            ),
            imageUrl = productDto.imageUrl,
            foodTypes = FoodTypes(productDto.foodTypes.map { Food(it) }),
            status = ProductStatus.fromValue(productDto.status!!)
        )

        val savedProduct: Product = productPersistence.save(product)
        val changedInventory: InventoryDto = inventoryService.modifyInventory(productDto)

        return ProductDto.from(
            savedProduct,
            savedProduct.imageUrl?.let(fileStorageService::resolveImageUrl),
            changedInventory
        )
    }
}