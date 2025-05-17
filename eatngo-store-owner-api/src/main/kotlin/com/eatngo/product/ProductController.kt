package com.eatngo.product

import com.eatngo.product.dto.*
import com.eatngo.product.service.ProductService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductController(
    private val productService: ProductService
) {
    @PostMapping("/stores/{store-id}/products")
    fun createProduct(
        @PathVariable("store-id") storeId: Long,
        @RequestBody createProductRequestDto: CreateProductRequestDto
    ): CreateProductResponseDto {
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
}