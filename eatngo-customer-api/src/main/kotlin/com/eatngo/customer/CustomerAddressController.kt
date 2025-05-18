package com.eatngo.customer

import com.eatngo.customer.domain.CustomerAddress
import com.eatngo.customer.dto.AddAddressRequestDto
import com.eatngo.customer.service.CustomerAddressService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Customer Address", description = "유저의 주소를 관리하는 API")
@RestController
class CustomerAddressController (
    private val customerAddressService: CustomerAddressService
) {

    @Operation(summary = "주소 목록 API", description = "유저의 주소 목록을 조회하는 API")
    @GetMapping("/api/v1/customer/address")
    fun getAddressList(): ResponseEntity<List<CustomerAddress>> {
        return  ResponseEntity.ok(customerAddressService.getAddressList())
    }

    @Operation(summary = "주소 등록 API", description = "유저의 주소를 등록하는 API")
    @PostMapping("/api/v1/customer/address")
    fun addAddress(@RequestBody address: AddAddressRequestDto): ResponseEntity<Long/*AddressID*/> {
        return ResponseEntity.ok(customerAddressService.addAddress(
            CustomerAddress(
                id = 0L,
                address = address.address,
                point = address.point,
                addressType = address.addressType
            )
        ))
    }

    @Operation(summary = "주소 삭제 API", description = "유저의 주소를 삭제하는 API")
    @DeleteMapping("/api/v1/customer/address/{addressId}")
    fun deleteAddress(@PathVariable addressId: Long): ResponseEntity<Boolean> {
        return  ResponseEntity.ok(customerAddressService.deleteAddress(addressId))
    }
}