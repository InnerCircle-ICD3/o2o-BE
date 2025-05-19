package com.eatngo.product

import com.eatngo.product.dto.*
import com.eatngo.product.service.ProductService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
class ProductController(
    private val productService: ProductService,
) {
    @PostMapping("/stores/{store-id}/products")
    fun createProduct(
        @PathVariable("store-id") storeId: Long,
        @RequestBody createProductRequestDto: CreateProductRequestDto
    ): GetProductDetailsResponseDto {
        val productDto: ProductDto = productService.createProduct(
            ProductDto(
                name = createProductRequestDto.name,
                description = createProductRequestDto.description,
                size = createProductRequestDto.size,
                inventory = ProductInventoryDto(createProductRequestDto.quantity),
                price = ProductPriceDto(createProductRequestDto.originalPrice),
                imageUrl = createProductRequestDto.image,
                storeId = storeId,
                foodTypes = createProductRequestDto.foodType,
            )
        )

        return CreateProductResponseDto.from(productDto)
    }

    @GetMapping("/stores/{store-id}/products/{product-id}")
    fun getProductDetails(
        @PathVariable("store-id") storeId: Long,
        @PathVariable("product-id") productId: Long
    ): GetProductDetailsResponseDto = GetProductDetailsResponseDto.from(
        productService.getProductDetails(storeId, productId)
    )

    @GetMapping("/stores/{store-id}/products")
    fun getAllProducts(
        @PathVariable("store-id") storeId: Long
    ): List<GetProductDetailsResponseDto> = productService.findAllProducts(storeId)
        .map { GetProductDetailsResponseDto.from(it) }

    @DeleteMapping("/stores/{store-id}/products/{product-id}")
    fun deleteProduct(
        @PathVariable("store-id") storeId: Long,
        @PathVariable("product-id") productId: Long,
    ): Unit = productService.deleteProduct(storeId, productId)

    @PostMapping("/stores/{store-id}/products/inventory")
    fun toggleStock(
        @PathVariable("store-id") storeId: Long,
        @Valid @RequestBody request: ToggleStockRequestDto
    ): ToggleStockResponseDto {
        if (request.action !in listOf("increase", "decrease")) {
            throw IllegalArgumentException("잘못된 action type 입니다. ${request.action}")
        }

        return ToggleStockResponseDto.from(
            productService.toggleStock(
                ProductCurrentStockDto(
                    id = request.id,
                    action = request.action,
                    amount = request.amount
                )
            )
        )
    }

    @PostMapping("/stores/{store-id}/products/{product-id}")
    fun updateProduct(
        @PathVariable("store-id") storeId: Long,
        @PathVariable("product-id") productId: Long,
        @Valid @RequestBody updateProductRequestDto: UpdateProductRequestDto
    ): UpdateProductResponseDto {
        val productDto = productService.modifyProduct(
            ProductDto(
                id = productId,
                name = updateProductRequestDto.name,
                description = updateProductRequestDto.description,
                size = updateProductRequestDto.size,
                inventory = ProductInventoryDto(
                    updateProductRequestDto.inventory.quantity,
                    updateProductRequestDto.inventory.stock
                ),
                price = ProductPriceDto(
                    updateProductRequestDto.price.originalPrice,
                    updateProductRequestDto.price.discountRate
                ),
                imageUrl = updateProductRequestDto.image,
                storeId = storeId,
                foodTypes = updateProductRequestDto.foodType,
                status = updateProductRequestDto.status
            )
        )

        return UpdateProductResponseDto.from(productDto)
    }

}
