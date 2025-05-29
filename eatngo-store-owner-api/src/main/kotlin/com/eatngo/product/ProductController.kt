package com.eatngo.product

import com.eatngo.common.response.ApiResponse
import com.eatngo.inventory.dto.InventoryDto
import com.eatngo.product.domain.StockActionType
import com.eatngo.product.domain.StockActionType.DECREASE
import com.eatngo.product.domain.StockActionType.INCREASE
import com.eatngo.product.dto.*
import com.eatngo.product.service.ProductService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@Tag(name = "상품", description = "상품 관련 API")
@RestController
class ProductController(
    private val productService: ProductService,
) {
    @PostMapping("/stores/{store-id}/products")
    @Operation(summary = "상품 생성", description = "상품 생성")
    fun createProduct(
        @PathVariable("store-id") storeId: Long,
        @RequestBody createProductRequestDto: CreateProductRequestDto
    ): ApiResponse<CreateProductResponseDto> {
        val productDto: ProductDto = productService.createProduct(
            ProductDto(
                name = createProductRequestDto.name,
                description = createProductRequestDto.description,
                size = createProductRequestDto.size,
                inventory = InventoryDto(createProductRequestDto.quantity),
                price = ProductPriceDto(createProductRequestDto.originalPrice),
                imageUrl = createProductRequestDto.image,
                storeId = storeId,
                foodTypes = createProductRequestDto.foodType,
            )
        )

        return ApiResponse.success(CreateProductResponseDto.from(productDto))
    }

    @GetMapping("/stores/{store-id}/products/{product-id}")
    @Operation(summary = "상품 상세 조회", description = "상품 상세 조회")
    fun getProductDetails(
        @PathVariable("store-id") storeId: Long,
        @PathVariable("product-id") productId: Long
    ): ApiResponse<GetProductDetailsResponseDto> = ApiResponse.success(
        GetProductDetailsResponseDto.from(
            productService.getProductDetails(storeId, productId)
        )
    )

    @GetMapping("/stores/{store-id}/products")
    @Operation(summary = "상품 목록 조회", description = "상품 목록 조회")
    fun getAllProducts(
        @PathVariable("store-id") storeId: Long
    ): ApiResponse<List<GetProductDetailsResponseDto>> = ApiResponse.success(
        productService.findAllProducts(storeId)
            .map { GetProductDetailsResponseDto.from(it) }
    )

    @DeleteMapping("/stores/{store-id}/products/{product-id}")
    @Operation(summary = "상품 삭제", description = "상품 삭제")
    fun deleteProduct(
        @PathVariable("store-id") storeId: Long,
        @PathVariable("product-id") productId: Long,
    ): ApiResponse<Unit> = ApiResponse.success(
        productService.deleteProduct(storeId, productId)
    )

    @PostMapping("/stores/{store-id}/products/inventory")
    @Operation(summary = "상품 재고 증가/감소 기능", description = "상품 재고 증가/감소 기능")
    fun toggleStock(
        @PathVariable("store-id") storeId: Long,
        @Valid @RequestBody request: ToggleStockRequestDto
    ): ApiResponse<ToggleStockResponseDto> {
        if (StockActionType.fromValue(request.action) !in listOf(INCREASE, DECREASE)) {
            throw IllegalArgumentException("잘못된 action type 입니다. ${request.action}")
        }

        return ApiResponse.success(
            ToggleStockResponseDto.from(
                productService.toggleStock(
                    ProductCurrentStockDto(
                        id = request.id,
                        action = request.action,
                        amount = request.amount
                    )
                )
            )
        )
    }

    @PostMapping("/stores/{store-id}/products/{product-id}")
    @Operation(summary = "상품 수정", description = "상품 수정")
    fun updateProduct(
        @PathVariable("store-id") storeId: Long,
        @PathVariable("product-id") productId: Long,
        @Valid @RequestBody updateProductRequestDto: UpdateProductRequestDto
    ): ApiResponse<UpdateProductResponseDto> {
        val productDto = productService.modifyProduct(
            ProductDto(
                id = productId,
                name = updateProductRequestDto.name,
                description = updateProductRequestDto.description,
                size = updateProductRequestDto.size,
                inventory = InventoryDto(
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

        return ApiResponse.success(UpdateProductResponseDto.from(productDto))
    }

}
