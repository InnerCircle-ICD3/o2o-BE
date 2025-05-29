package com.eatngo.product.service

import com.eatngo.file.FileStorageService
import com.eatngo.product.domain.*
import com.eatngo.product.domain.Product.*
import com.eatngo.product.domain.ProductSizeType.*
import com.eatngo.product.domain.StockActionType.DECREASE
import com.eatngo.product.domain.StockActionType.INCREASE
import com.eatngo.product.dto.ProductAfterStockDto
import com.eatngo.product.dto.ProductCurrentStockDto
import com.eatngo.product.dto.ProductDto
import com.eatngo.product.infra.ProductCachePersistence
import com.eatngo.product.infra.ProductPersistence
import com.eatngo.product.infra.findByIdAndStoreIdOrElseThrow
import com.eatngo.product.infra.findByIdOrThrow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductService(
    private val productPersistence: ProductPersistence,
    private val fileStorageService: FileStorageService,
    private val productCachePersistence: ProductCachePersistence
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
        productCachePersistence.save(product)

        return ProductDto.from(
            savedProduct,
            savedProduct.imageUrl?.let(fileStorageService::resolveImageUrl)
        )
    }

    fun getProductDetails(
        storeId: Long,
        productId: Long
    ): ProductDto {
        // TODO storePersistence.findById(storeId)
        val product: Product =
            productCachePersistence.findById(productId) ?: productPersistence.findByIdOrThrow(productId)

        return ProductDto.from(
            product,
            product.imageUrl?.let(fileStorageService::resolveImageUrl)
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
                    it.imageUrl?.let(fileStorageService::resolveImageUrl)
                )
            }
    }

    fun deleteProduct(storeId: Long, productId: Long) {
        val product: Product = productPersistence.findByIdOrThrow(productId)
        product.remove()
        productPersistence.save(product)
        productCachePersistence.deleteById(productId)
    }

    fun toggleStock(productCurrentStockDto: ProductCurrentStockDto): ProductAfterStockDto {
        val product: Product = productPersistence.findByIdOrThrow(productCurrentStockDto.id)
        product.changeStock(productCurrentStockDto.action, productCurrentStockDto.amount)
        val savedProduct = productPersistence.save(product)

        when (StockActionType.fromValue(productCurrentStockDto.action)) {
            INCREASE -> productCachePersistence.increaseStock(product.id!!, productCurrentStockDto.amount)
            DECREASE -> productCachePersistence.decreaseStock(product.id!!, productCurrentStockDto.amount)
        }
        return ProductAfterStockDto.create(savedProduct)
    }

    fun modifyProduct(productDto: ProductDto): ProductDto {
        val product: Product = productPersistence.findByIdAndStoreIdOrElseThrow(productDto.id!!, productDto.storeId)
        product.modify(
            name = productDto.name,
            description = productDto.description,
            inventory = Inventory(
                productDto.inventory.quantity,
                productDto.inventory.stock
            ),
            price = ProductPrice(
                productDto.price.originalPrice,
                productDto.price.discountRate
            ),
            imageUrl = productDto.imageUrl,
            foodTypes = FoodTypes(productDto.foodTypes.map { Food(it) }),
            status = ProductStatus.fromValue(productDto.status!!)
        )

        val savedProduct: Product = productPersistence.save(product)
        productCachePersistence.deleteById(savedProduct.id!!)
        productCachePersistence.save(savedProduct)

        return ProductDto.from(
            savedProduct,
            savedProduct.imageUrl?.let(fileStorageService::resolveImageUrl)
        )
    }
}