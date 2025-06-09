package com.eatngo.customer

import com.eatngo.common.type.Address
import com.eatngo.common.type.CoordinateVO
import com.eatngo.configuration.TestConfiguration
import com.eatngo.customer.domain.CustomerAddressType
import com.eatngo.customer.dto.AddressCreateDto
import com.eatngo.customer.dto.CustomerAddressDto
import com.eatngo.helper.CustomerTestHelper
import com.eatngo.store.vo.LotNumberAddressVO
import com.eatngo.store.vo.RoadNameAddressVO
import com.eatngo.store.vo.ZipCodeVO
import io.kotest.core.annotation.Tags
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import


@Tags("integration")
@Import(TestConfiguration::class, CustomerTestHelper::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerAddressControllerTest(
    @LocalServerPort
    val port: Int,
    private val customerTestHelper: CustomerTestHelper
) : DescribeSpec() {
    init {
        beforeSpec {
            RestAssured.port = port
            RestAssured.basePath = "/api/v1/customers/address"
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        }

        afterSpec {
            RestAssured.reset()
        }

        describe("POST /api/v1/customers/address") {
            it("고객 주소 정보를 성공적으로 생성한다") {
                val (token, loginCustomer) = customerTestHelper.유저_생성_및_토큰_반환()

                val customerAddressId = 고객_주소_정보_생성_성공(token)

                customerAddressId shouldBeGreaterThan 0L

                customerTestHelper.데이터_삭제(loginCustomer)
            }
        }

        describe("GET /api/v1/customers/address") {
            it("고객 주소 정보를 성공적으로 조회한다") {
                val (token, loginCustomer) = customerTestHelper.유저_생성_및_토큰_반환()

                고객_주소_정보_생성_성공(token)

                val customerDtos = 고객_주소_정보_조회_성공(token)
                val addressCreateDto = addressCreateDto()
                customerDtos.size shouldBe 1
                customerDtos[0] shouldBe CustomerAddressDto(
                    id = customerDtos[0].id,
                    customerId = loginCustomer.customerId,
                    radiusInKilometers = 1.5,
                    roadNameAddress = addressCreateDto.address.roadNameAddress?.let { RoadNameAddressVO.from(it.value) },
                    lotNumberAddress = LotNumberAddressVO.from(addressCreateDto.address.lotNumberAddress.value),
                    buildingName = addressCreateDto.address.buildingName,
                    zipCode = ZipCodeVO.from(addressCreateDto.address.zipCode.value),
                    region1DepthName = addressCreateDto.address.region1DepthName,
                    region2DepthName = addressCreateDto.address.region2DepthName,
                    region3DepthName = addressCreateDto.address.region3DepthName,
                    latitude = addressCreateDto.address.coordinate.latitude,
                    longitude = addressCreateDto.address.coordinate.longitude,
                    customerAddressType = addressCreateDto.customerAddressType,
                    description = addressCreateDto.description
                )

                customerTestHelper.데이터_삭제(loginCustomer)
            }
        }

        describe("DELETE /api/v1/customers/address/{addressId}") {
            it("고객 주소 삭제에 성공한다") {
                val (token, loginCustomer) = customerTestHelper.유저_생성_및_토큰_반환()
                val customerAddressId = 고객_주소_정보_생성_성공(token)
                고객_주소_정보_조회_성공(token)

                고객_주소_삭제(token, customerAddressId)

                고객_주소_정보_찾을_수_없음(token)

                customerTestHelper.데이터_삭제(loginCustomer)
            }
        }
    }

    private fun 고객_주소_정보_생성_성공(token: String): Long {
        val addressCreateDto = addressCreateDto()

        val response = RestAssured.given()
            .cookie("access_token", token)
            .contentType(ContentType.JSON)
            .body(addressCreateDto)
            .`when`()
            .post()
            .then()
            .statusCode(200)
            .extract()

        val success = response.jsonPath().getBoolean("success")
        if (!success) {
            throw AssertionError("응답 실패: ${response.jsonPath().getString("message")}")
        }

        return response.jsonPath().getLong("data")

    }

    private fun addressCreateDto() = AddressCreateDto(
        address = Address(
            RoadNameAddressVO.from("서울특별시 강남구 테헤란로 123"),
            LotNumberAddressVO.from("서울특별시 강남구 역삼동 456-7"),
            "강남역 1번 출구",
            ZipCodeVO.from("12345"),
            "서울특별시",
            "강남구",
            "역삼동",
            CoordinateVO(
                Pair(37.123455, 127.12346),
            )
        ),
        customerAddressType = CustomerAddressType.HOME,
        distanceInKilometers = 1.5, // 주소 반경 (단위: km)
        description = "집 주소"
    )


    private fun 고객_주소_정보_조회_성공(token: String): List<CustomerAddressDto> {
        val response = RestAssured.given()
            .cookie("access_token", token)
            .`when`()
            .get()
            .then()
            .statusCode(200)
            .extract()

        val success = response.jsonPath().getBoolean("success")
        if (!success) {
            throw AssertionError("응답 실패: ${response.jsonPath().getString("message")}")
        }

        return response.jsonPath().getList("data", CustomerAddressDto::class.java)
    }

    private fun 고객_주소_삭제(token: String, addressId: Long) {
        RestAssured.given()
            .cookie("access_token", token)
            .`when`()
            .delete("/$addressId")
            .then()
            .statusCode(204)
    }

    private fun 고객_주소_정보_찾을_수_없음(token: String) {
        val response = RestAssured.given()
            .cookie("access_token", token)
            .`when`()
            .get()
            .then()
            .statusCode(200)
            .extract()
        val success = response.jsonPath().getBoolean("success")
        if (!success) {
            throw AssertionError("응답 실패: ${response.jsonPath().getString("message")}")
        }

        response.jsonPath().getList("data", CustomerAddressDto::class.java) shouldBe emptyList()
    }
}