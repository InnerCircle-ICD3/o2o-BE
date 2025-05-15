package com.eatngo.customer

import com.eatngo.common.response.ApiResponse
import com.eatngo.customer.domain.CustomerAddress
import com.eatngo.customer.dto.AddAddressRequestDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Customer Address", description = "유저의 주소를 관리하는 API")
@RestController
class CustomerAddressController {

    @Operation(summary = "주소 목록 API", description = "유저의 주소 목록을 조회하는 API")
    @GetMapping("/customer/address")
    fun getAddressList(): ApiResponse<List<CustomerAddress>> {
        // TODO : 주소 목록 API 구현
        return ApiResponse.success(emptyList())
    }

    @Operation(summary = "주소 등록 API", description = "유저의 주소를 등록하는 API")
    @PostMapping("/customer/address")
    fun addAddress(@RequestBody address: AddAddressRequestDto): ApiResponse<Long/*AddressID*/> {
        // TODO : 주소 등록 API 구현
        return ApiResponse.success(1L)
    }

    @Operation(summary = "주소 삭제 API", description = "유저의 주소를 삭제하는 API")
    @DeleteMapping("/customer/address/{addressId}")
    fun deleteAddress(@PathVariable addressId: Long): ApiResponse<Boolean> {
        // TODO : 주소 삭제 API 구현
        return ApiResponse.success(true)
    }
}