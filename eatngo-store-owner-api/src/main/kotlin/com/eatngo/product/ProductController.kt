package com.eatngo.product

import com.eatngo.product.dto.CreateProductRequestDto
import com.eatngo.product.dto.ProductCreateDto
import com.eatngo.product.dto.ProductInventoryCreateDto
import com.eatngo.product.dto.ProductPriceCreateDto
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
            ProductCreateDto(
                name = createProductRequestDto.name,
                description = createProductRequestDto.description,
                size = createProductRequestDto.size,
                inventory = ProductInventoryCreateDto(createProductRequestDto.quantity),
                price = ProductPriceCreateDto(createProductRequestDto.originalPrice),
                imageUrl = imageUrl,
                storeId = storeId,
                foodTypes = createProductRequestDto.foodType,
            )
        )
    }
}