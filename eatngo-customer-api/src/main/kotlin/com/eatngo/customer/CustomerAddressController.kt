package com.eatngo.customer

import com.eatngo.auth.annotation.CustomerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.customer.dto.AddressCreateDto
import com.eatngo.customer.dto.CustomerAddressDto
import com.eatngo.customer.service.CustomerAddressService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Customer Address", description = "유저의 주소를 관리하는 API")
@RequestMapping("/api/v1/customers/address")
@RestController
class CustomerAddressController(
    private val customerAddressService: CustomerAddressService,
) {
    @Operation(summary = "주소 목록 API", description = "유저의 주소 목록을 조회하는 API")
    @GetMapping
    fun getAddressList(
        @CustomerId customerId: Long,
    ): ResponseEntity<ApiResponse<List<CustomerAddressDto>>> {
        return ResponseEntity.ok(
            ApiResponse.success(
                customerAddressService.getAddressList(customerId)
            )
        )
    }

    @Operation(summary = "주소 등록 API", description = "유저의 주소를 등록하는 API")
    @PostMapping
    fun addAddress(
        @CustomerId customerId: Long,
        @RequestBody addressCreateDto: AddressCreateDto,
    ): ResponseEntity<ApiResponse<Long>> =
        ResponseEntity.ok(
            ApiResponse.success(
                customerAddressService.addAddress(customerId, addressCreateDto)
            )
        )

    @Operation(summary = "주소 삭제 API", description = "유저의 주소를 삭제하는 API")
    @DeleteMapping("/{addressId}")
    fun deleteAddress(
        @CustomerId customerId: Long,
        @PathVariable addressId: Long,
    ): ResponseEntity<Unit> {
        customerAddressService.deleteAddress(customerId, addressId)
        return ResponseEntity.noContent().build()
    }
}
