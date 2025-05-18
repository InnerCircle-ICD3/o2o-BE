package com.eatngo.product

import com.eatngo.product.dto.*
import com.eatngo.product.service.ProductService
import jakarta.validation.Valid
import org.springdoc.webmvc.core.service.RequestService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

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

        return ToggleStockResponseDto.create(
            productService.toggleStock(
                ProductCurrentStockDto(
                    id = request.id,
                    action = request.action,
                    amount = request.amount
                )
            )
        )
    }
}
