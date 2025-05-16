package com.eatngo.product

import com.eatngo.product.dto.*
import com.eatngo.product.service.ProductService
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
class ProductController(
    private val productService: ProductService
) {
    @PostMapping("/stores/{store-id}/products")
    fun createProduct(
        @PathVariable("store-id") storeId: Long,
        @RequestParam("image") image: MultipartFile,
        @ModelAttribute createProductRequestDto: CreateProductRequestDto
    ): CreateProductResponseDto {
        val productDto: ProductDto = productService.createProduct(
            ProductDto(
                name = createProductRequestDto.name,
                description = createProductRequestDto.description,
                size = createProductRequestDto.size,
                inventory = ProductInventoryDto(createProductRequestDto.quantity),
                price = ProductPriceDto(createProductRequestDto.originalPrice),
                storeId = storeId,
                foodTypes = createProductRequestDto.foodType,
            ),
            image
        )

        return CreateProductResponseDto.from(productDto)
    }
}