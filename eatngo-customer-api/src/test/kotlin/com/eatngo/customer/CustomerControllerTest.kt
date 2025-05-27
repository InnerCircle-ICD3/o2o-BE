package com.eatngo.customer

import com.eatngo.auth.dto.LoginCustomer
import com.eatngo.auth.token.TokenProvider
import com.eatngo.configuration.TestConfiguration
import com.eatngo.customer.dto.CustomerDto
import com.eatngo.customer.dto.CustomerUpdateDto
import com.eatngo.customer.service.CustomerService
import com.eatngo.user_account.oauth2.constants.Oauth2Provider
import com.eatngo.user_account.oauth2.dto.KakaoOauth2
import com.eatngo.user_account.service.UserAccountService
import com.eatngo.user_account.vo.Nickname
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import

@Import(TestConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerTest(
    @LocalServerPort val port: Int,
    private val userAccountService: UserAccountService,
    private val customerService: CustomerService,
    private val tokenProvider: TokenProvider,
) : DescribeSpec() {

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
                val (token, loginCustomer) = 유저_생성_및_토큰_반환()

                val customerDto = 고객_정보_조회_성공(token)

                customerDto.nickname shouldBe "홍길동"

                데이터_삭제(loginCustomer)
            }
        }

        describe("PATCH /api/v1/customers/modify") {
            it("고객 정보를 수정한다") {
                val (token, loginCustomer) = 유저_생성_및_토큰_반환()

                val updateDto = CustomerUpdateDto(nickname = Nickname("엄복동"))
                고객_정보_수정(token, updateDto)

                val customerDto = 고객_정보_조회_성공(token)
                customerDto.nickname shouldBe "엄복동"

                데이터_삭제(loginCustomer)
            }
        }

        describe("DELETE /api/v1/customers/sign-out") {
            it("고객 탈퇴에 성공한다") {
                val (token, loginCustomer) = 유저_생성_및_토큰_반환()

                고객_탈퇴(token)

                고객_정보_찾을_수_없음(token)

                데이터_삭제(loginCustomer)
            }
        }
    }

    private fun 유저_생성_및_토큰_반환(): Pair<String, LoginCustomer> {
        val account = userAccountService.createAccount(
            KakaoOauth2(
                mapOf(
                    "id" to System.currentTimeMillis(),
                    "kakao_account" to mapOf(
                        "email" to "test${System.nanoTime()}@test.com",
                        "profile" to mapOf("nickname" to "홍길동")
                    )
                ),
                Oauth2Provider.KAKAO
            )
        )

        val customer = customerService.createByAccount(account)

        val loginCustomer = LoginCustomer(
            userAccountId = account.id,
            roles = account.roles.map { it.name },
            customerId = customer.id
        )

        val token = tokenProvider.createAccessToken(loginCustomer)
        return token to loginCustomer
    }

    private fun 데이터_삭제(loginCustomer: LoginCustomer) {
        try {
            customerService.deleteCustomer(loginCustomer.customerId)
            userAccountService.deleteAccount(loginCustomer.userAccountId)
        } catch (e: Exception) {
            println("데이터 삭제 실패: ${e.message}")
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