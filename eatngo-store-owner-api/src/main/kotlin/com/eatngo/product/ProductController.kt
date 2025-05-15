package com.eatngo.product

import com.eatngo.product.dto.CreateProductRequestDto
import com.eatngo.product.dto.ProductDto
import com.eatngo.product.dto.ProductInventoryDto
import com.eatngo.product.dto.ProductPriceDto
import com.eatngo.product.service.ProductService
import org.springframework.web.bind.annotation.*

@RestController
class ProductController(
    private val productService: ProductService
) {
    @PostMapping("/stores/{store-id}/products")
    fun createProduct(
        @PathVariable("store-id") storeId: Long,
        @RequestParam("image-url") imageUrl: String,
        @ModelAttribute createProductRequestDto: CreateProductRequestDto
    ) {
        productService.createProduct(
            ProductDto(
                name = createProductRequestDto.name,
                description = createProductRequestDto.description,
                size = createProductRequestDto.size,
                inventory = ProductInventoryDto(createProductRequestDto.quantity),
                price = ProductPriceDto(createProductRequestDto.originalPrice),
                imageUrl = imageUrl,
                storeId = storeId,
                foodTypes = createProductRequestDto.foodType,
            )
        )
    }
}