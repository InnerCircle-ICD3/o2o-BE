package com.eatngo.customer

import com.eatngo.auth.annotation.CustomerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.customer.dto.CustomerDto
import com.eatngo.customer.dto.CustomerUpdateDto
import com.eatngo.customer.service.CustomerService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Customer", description = "유저 정보를 관리하는 API")
@RestController
@RequestMapping("/api/v1/customers")
class CustomerController(
    private val customerService: CustomerService,
) {
    @Operation(summary = "고객 정보 조회 API", description = "고객 정보를 조회하는 API")
    @GetMapping("/me")
    fun getCustomer(
        @CustomerId customerId: Long,
    ): ResponseEntity<ApiResponse<CustomerDto>> {
        val customerDto = customerService.getCustomerById(customerId)
            .let { CustomerDto.from(it) }
        return ResponseEntity.ok(ApiResponse.success(customerDto))
    }

    @Operation(summary = "탈퇴 API", description = "유저의 정보를 삭제하는 API")
    @DeleteMapping("/sign-out")
    fun deleteCustomer(
        @CustomerId customerId: Long,
    ): ResponseEntity<Unit> {
        customerService.deleteCustomer(customerId)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "고객 정보 수정 API", description = "유저의 정보를 수정하는 API")
    @PatchMapping("/modify")
    fun updateCustomer(
        @CustomerId customerId: Long,
        @RequestBody customerUpdateDto: CustomerUpdateDto
    ): ResponseEntity<Unit> {
        customerService.update(customerId, customerUpdateDto)
        return ResponseEntity.noContent().build()
    }
}
