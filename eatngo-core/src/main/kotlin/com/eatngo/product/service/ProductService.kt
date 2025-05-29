package com.eatngo.product.service

import com.eatngo.common.exception.InventoryException.InventoryNotFound
import com.eatngo.common.exception.ProductException.ProductNotFound
import com.eatngo.extension.orThrow
import com.eatngo.file.FileStorageService
import com.eatngo.product.domain.*
import com.eatngo.product.domain.Product.*
import com.eatngo.product.domain.ProductSizeType.*
import com.eatngo.product.dto.ProductAfterStockDto
import com.eatngo.product.dto.ProductCurrentStockDto
import com.eatngo.product.dto.ProductDto
import com.eatngo.product.infra.InventoryPersistence
import com.eatngo.product.infra.ProductCachePersistence
import com.eatngo.product.infra.ProductPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductService(
    private val productPersistence: ProductPersistence,
    private val fileStorageService: FileStorageService,
    private val productCachePersistence: ProductCachePersistence,
    private val inventoryPersistence: InventoryPersistence,
    // TODO storeRepository
) {
    fun createProduct(
        productDto: ProductDto,
    ): ProductDto {
        // TODO storeRepo.findById()

        val inventory = Inventory.create(productDto.inventory.quantity, productDto.id!!)
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
        val savedInventory: Inventory = inventoryPersistence.save(inventory)
        productCachePersistence.save(product)

        return ProductDto.from(
            savedProduct,
            savedProduct.imageUrl?.let(fileStorageService::resolveImageUrl),
            savedInventory
        )
    }

    fun getProductDetails(
        storeId: Long,
        productId: Long
    ): ProductDto {
        // TODO storePersistence.findById(storeId)
        val product: Product =
            productCachePersistence.findById(productId) ?: productPersistence.findById(productId)
                .orThrow { ProductNotFound(productId) }

        val inventory: Inventory =
            inventoryPersistence.findTopByProductIdOrderByVersionDesc(productId)
                .orThrow { InventoryNotFound(productId) }

        return ProductDto.from(
            product,
            product.imageUrl?.let(fileStorageService::resolveImageUrl),
            inventory
        )
    }

    fun findAllProducts(storeId: Long): List<ProductDto> {
        var products: List<Product> = productCachePersistence.findAllByStoreId(storeId)
        if (products.isEmpty()) {
            products = productPersistence.findAllByStoreId(storeId)
        }

        return products
            .map {
                ProductDto.from(
                    it,
                    it.imageUrl?.let(fileStorageService::resolveImageUrl),
                    inventoryPersistence.findTopByProductIdOrderByVersionDesc(it.id)
                        .orThrow { InventoryNotFound(it.id) }
                )
            }
    }

    fun deleteProduct(storeId: Long, productId: Long) {
        val product: Product = productPersistence.findById(productId).orThrow { ProductNotFound(productId) }
        product.remove()
        productPersistence.save(product)
        productCachePersistence.deleteById(productId)
        inventoryPersistence.deleteById(product.id)
    }

    fun toggleStock(productCurrentStockDto: ProductCurrentStockDto): ProductAfterStockDto {
        val product: Product = productPersistence.findById(productCurrentStockDto.id)
            .orThrow { ProductNotFound(productCurrentStockDto.id) }

        val inventory: Inventory = inventoryPersistence.findTopByProductIdOrderByVersionDesc(product.id)
            .orThrow { InventoryNotFound(productCurrentStockDto.id) }
        val changedInventory = inventory.changeStock(productCurrentStockDto.action, productCurrentStockDto.amount)

        val savedProduct = productPersistence.save(product)
        val savedInventory: Inventory = inventoryPersistence.save(changedInventory)

        return ProductAfterStockDto.create(savedProduct, savedInventory)
    }

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
        productCachePersistence.deleteById(savedProduct.id)
        productCachePersistence.save(savedProduct)

        val inventory: Inventory = inventoryPersistence.findTopByProductIdOrderByVersionDesc(productDto.id!!)
            .orThrow { InventoryNotFound(productDto.id!!) }

        val changedInventory: Inventory = inventory.changeInventory(
            quantity = productDto.inventory.quantity,
            stock = productDto.inventory.stock,
        )

        val savedInventory: Inventory = inventoryPersistence.save(changedInventory)

        return ProductDto.from(
            savedProduct,
            savedProduct.imageUrl?.let(fileStorageService::resolveImageUrl),
            savedInventory
        )
    }
}