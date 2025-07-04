package com.eatngo.product.service

import com.eatngo.common.exception.product.ProductException.ProductNotFound
import com.eatngo.common.exception.store.StoreException.StoreNotFound
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
class ProductService(
    private val productPersistence: ProductPersistence,
    private val fileStorageService: FileStorageService,
    private val inventoryService: InventoryService,
    private val storePersistence: StorePersistence,
    private val storeProductValidator: StoreProductValidator
) {

    @Caching(
        put = [CachePut("product", key = "#result.id")],
        evict = [CacheEvict("storeProducts", key = "#productDto.storeId")]
    )
    @Transactional
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
        storeProductValidator.validateProduct(storeId = store.id)

        productDto.id = savedProduct.id
        val savedInventory: InventoryDto = inventoryService.createInventory(productDto)

        return ProductDto.from(
            savedProduct,
            savedProduct.imageUrl?.let(fileStorageService::resolveImageUrl),
            savedInventory,
            store.name.value
        )
    }

    @Cacheable("product", key = "#productId")
    @Transactional
    fun getProductDetails(
        storeId: Long,
        productId: Long
    ): ProductDto {
        val store: Store = storePersistence.findById(storeId)
            .orThrow { StoreNotFound(storeId) }
        val product: Product = productPersistence.findActivatedProductByIdAndStoreId(productId, storeId)
            .orThrow { ProductNotFound(productId) }

        val inventoryDetails: InventoryDto = inventoryService.getInventoryDetails(productId)

        return ProductDto.from(
            product,
            product.imageUrl?.let(fileStorageService::resolveImageUrl),
            inventoryDetails,
            store.name.value
        )
    }

    @Cacheable("storeProducts", key = "#storeId")
    @Transactional
    fun findAllProducts(storeId: Long): List<ProductDto> {
        val store: Store = storePersistence.findById(storeId)
            .orThrow { StoreNotFound(storeId) }
        return productPersistence.findAllActivatedProductByStoreId(storeId)
            .map {
                ProductDto.from(
                    it,
                    it.imageUrl?.let(fileStorageService::resolveImageUrl),
                    inventoryService.getInventoryDetails(it.id),
                    store.name.value
                )
            }
    }

    @Caching(
        evict = [
            CacheEvict("product", key = "#productId"),
            CacheEvict("inventory", key = "#productId"),
            CacheEvict("storeProducts", key = "#storeId")],
    )
    @Transactional
    fun deleteProduct(storeId: Long, productId: Long) {
        val product: Product =
            productPersistence.findActivatedProductById(productId).orThrow { ProductNotFound(productId) }
        product.remove()
        productPersistence.save(product)
        inventoryService.deleteInventory(product.id)
    }

    @CacheEvict("product", key = "#productCurrentStockDto.id")
    @Transactional
    fun toggleStock(productCurrentStockDto: ProductCurrentStockDto): ProductAfterStockDto {
        val store: Store = storePersistence.findById(productCurrentStockDto.storeId)
            .orThrow { StoreNotFound(productCurrentStockDto.storeId) }
        val product: Product = productPersistence.findActivatedProductById(productCurrentStockDto.id)
            .orThrow { ProductNotFound(productCurrentStockDto.id) }

        val changedInventory: InventoryDto = inventoryService.toggleInventory(
            productCurrentStockDto,
            store.id,
            findTotalInitialStocks(store.id)
        )

        return ProductAfterStockDto.create(product, changedInventory)
    }

    @Caching(
        put = [CachePut("product", key = "#productDto.id")],
        evict = [CacheEvict("storeProducts", key = "#productDto.storeId")]
    )
    @Transactional
    fun modifyProduct(productDto: ProductDto): ProductDto {
        val store: Store = storePersistence.findById(productDto.storeId)
            .orThrow { StoreNotFound(productDto.storeId) }
        val product: Product =
            productPersistence.findActivatedProductByIdAndStoreId(productDto.id!!, productDto.storeId)
                .orThrow { ProductNotFound(productDto.id!!) }

        val changedProduct = product.modify(
            name = productDto.name,
            description = productDto.description,
            price = ProductPrice(
                productDto.price.originalPrice,
                productDto.price.discountRate
            ),
            imageUrl = productDto.imageUrl,
            foodTypes = FoodTypes(productDto.foodTypes.map { Food(it) }),
            status = ProductStatus.fromValue(productDto.status!!),
            size = productDto.size,
        )

        val savedProduct: Product = productPersistence.save(changedProduct)
        storeProductValidator.validateProduct(storeId = store.id)

        val initialStock = findTotalInitialStocks(savedProduct.storeId)
        val changedInventory: InventoryDto =
            inventoryService.modifyInventory(productDto, initialStock)

        return ProductDto.from(
            savedProduct,
            savedProduct.imageUrl?.let(fileStorageService::resolveImageUrl),
            changedInventory,
            store.name.value
        )
    }

    fun findTotalInitialStocks(storeId: Long): Int {
        return productPersistence.findAllActivatedProductByStoreId(storeId)
            .map { product ->
                inventoryService.getInventoryDetails(product.id)
            }
            .sumOf { i -> i.stock }
    }
}