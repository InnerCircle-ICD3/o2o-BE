package com.eatngo.product.service

import com.eatngo.common.exception.ProductException.ProductNotFound
import com.eatngo.common.exception.StoreException.StoreNotFound
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
import com.eatngo.store.domain.Store
import com.eatngo.store.infra.StorePersistence
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
    private val storePersistence: StorePersistence,
    private val storeTotalInventoryTypeDecider: StoreTotalInventoryTypeDecider
) {

    @Caching(
        put = [CachePut("product", key = "#result.id")],
        evict = [CacheEvict("storeProducts", key = "#productDto.storeId")]
    )
    fun createProduct(
        productDto: ProductDto,
    ): ProductDto {
        val store: Store = storePersistence.findById(productDto.storeId)
            .orThrow { StoreNotFound(productDto.storeId) }
        val price = ProductPrice.create(productDto.price.originalPrice)
        val foodTypes = FoodTypes.create(productDto.foodTypes)

        val product: Product = when (ProductSizeType.fromValue(productDto.size)) {
            L -> {
                LargeEatNGoBag(
                    name = productDto.name,
                    description = productDto.description,
                    price = price,
                    imageUrl = productDto.imageUrl,
                    storeId = store.id,
                    foodTypes = foodTypes,
                )
            }

            M -> {
                MediumEatNGoBag(
                    name = productDto.name,
                    description = productDto.description,
                    price = price,
                    imageUrl = productDto.imageUrl,
                    storeId = store.id,
                    foodTypes = foodTypes,
                )
            }

            S -> {
                SmallEatNGoBag(
                    name = productDto.name,
                    description = productDto.description,
                    price = price,
                    imageUrl = productDto.imageUrl,
                    storeId = store.id,
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
        val product: Product = productPersistence.findByIdAndStoreId(productId, storeId)
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
        return productPersistence.findAllByStoreId(storeId)
            .map {
                ProductDto.from(
                    it,
                    it.imageUrl?.let(fileStorageService::resolveImageUrl),
                    inventoryService.getInventoryDetails(it.id)
                )
            }
    }

    @Caching(
        evict = [
            CacheEvict("product", key = "#productId"),
            CacheEvict("inventory", key = "#productId"),
            CacheEvict("storeProducts", key = "#storeId")],
    )
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

        val store: Store = storePersistence.findById(product.storeId).orThrow { StoreNotFound(product.storeId) }
        val changedInventory: InventoryDto = inventoryService.toggleInventory(productCurrentStockDto, store.id)

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
        val changedInventory: InventoryDto =
            inventoryService.modifyInventory(productDto, findTotalInitialStocks(product.storeId))

        return ProductDto.from(
            savedProduct,
            savedProduct.imageUrl?.let(fileStorageService::resolveImageUrl),
            changedInventory
        )
    }

    fun findTotalInitialStocks(storeId: Long): Int {
        return productPersistence.findAllByStoreId(storeId)
            .map { product ->
                inventoryService.getInventoryDetails(product.id)
            }
            .sumOf { i -> i.stock }
    }
}