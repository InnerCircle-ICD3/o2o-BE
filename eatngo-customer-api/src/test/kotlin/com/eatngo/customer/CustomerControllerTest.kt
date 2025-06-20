package com.eatngo.customer

import com.eatngo.configuration.TestConfiguration
import com.eatngo.customer.dto.CustomerDto
import com.eatngo.customer.dto.CustomerUpdateDto
import com.eatngo.helper.CustomerTestHelper
import com.eatngo.oauth2.client.KakaoOAuth2Client
import com.eatngo.user_account.vo.Nickname
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.test.context.bean.override.mockito.MockitoBean

@Import(TestConfiguration::class, CustomerTestHelper::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerTest(
    @LocalServerPort val port: Int,
    private val customerTestHelper: CustomerTestHelper,
) : DescribeSpec() {

    @MockitoBean
    lateinit var kakaoOAuth2Client: KakaoOAuth2Client

    init {
        beforeSpec {
            RestAssured.port = port
            RestAssured.basePath = "/api/v1/customers"
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        }

        afterSpec {
            RestAssured.reset()
        }

        describe("GET /api/v1/customers/me") {
            it("고객 정보를 성공적으로 조회한다") {
                val (token, loginCustomer) = customerTestHelper.유저_생성_및_토큰_반환()

                val customerDto = 고객_정보_조회_성공(token)

                customerDto.nickname shouldBe "홍길동"

                customerTestHelper.데이터_삭제(loginCustomer)
            }
        }

        describe("PATCH /api/v1/customers/modify") {
            it("고객 정보를 수정한다") {
                val (token, loginCustomer) = customerTestHelper.유저_생성_및_토큰_반환()

                val updateDto = CustomerUpdateDto(nickname = Nickname("엄복동"))
                고객_정보_수정(token, updateDto)

                val customerDto = 고객_정보_조회_성공(token)
                customerDto.nickname shouldBe "엄복동"

                customerTestHelper.데이터_삭제(loginCustomer)
            }
        }

        describe("DELETE /api/v1/customers/sign-out") {
            it("고객 탈퇴에 성공한다") {
                val (token, loginCustomer) = customerTestHelper.유저_생성_및_토큰_반환()

                고객_탈퇴(token)

                고객_정보_찾을_수_없음(token)

                customerTestHelper.데이터_삭제(loginCustomer)
            }
        }
    }


    private fun 고객_정보_조회_성공(token: String): CustomerDto {
        val response = RestAssured.given()
            .cookie("access_token", token)
            .`when`()
            .get("/me")
            .then()
            .statusCode(200)
            .extract()

        val success = response.jsonPath().getBoolean("success")
        if (!success) {
            throw AssertionError("응답 실패: ${response.jsonPath().getString("message")}")
        }

        return response.jsonPath().getObject("data", CustomerDto::class.java)
    }

    private fun 고객_정보_수정(token: String, updateDto: CustomerUpdateDto) {
        RestAssured.given()
            .cookie("access_token", token)
            .contentType(ContentType.JSON)
            .body(updateDto)
            .`when`()
            .patch("/modify")
            .then()
            .statusCode(204)
    }

    private fun 고객_탈퇴(token: String) {
        RestAssured.given()
            .cookie("access_token", token)
            .`when`()
            .delete("/sign-out")
            .then()
            .statusCode(204)
    }

    private fun 고객_정보_찾을_수_없음(token: String) {
        RestAssured.given()
            .cookie("access_token", token)
            .`when`()
            .get("/me")
            .then()
            .statusCode(404)
    }
}