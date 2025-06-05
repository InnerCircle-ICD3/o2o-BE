package com.eatngo.store_owner

import com.eatngo.auth.dto.LoginStoreOwner
import com.eatngo.auth.token.TokenProvider
import com.eatngo.configuration.TestConfiguration
import com.eatngo.store_owner.dto.StoreOwnerDto
import com.eatngo.store_owner.dto.StoreOwnerUpdateDto
import com.eatngo.store_owner.service.StoreOwnerService
import com.eatngo.user_account.oauth2.constants.Oauth2Provider
import com.eatngo.user_account.oauth2.dto.KakaoOAuth2
import com.eatngo.user_account.service.UserAccountService
import com.eatngo.user_account.vo.Nickname
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import java.time.LocalDateTime

@Import(TestConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StoreOwnerControllerTest(
    @LocalServerPort val port: Int,
    private val userAccountService: UserAccountService,
    private val storeOwnerService: StoreOwnerService,
    private val tokenProvider: TokenProvider,
) : DescribeSpec() {

    init {
        beforeSpec {
            RestAssured.port = port
            RestAssured.basePath = "/api/v1/store-owners"
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        }

        afterSpec {
            RestAssured.reset()
        }

        describe("GET /api/v1/store-owners/me") {
            it("점주 정보를 성공적으로 조회한다") {
                val (token, loginStoreOwner) = 유저_생성_및_토큰_반환()

                val storeOwnerDto = 점주_정보_조회_성공(token)

                storeOwnerDto.nickname shouldBe "홍길동"

                데이터_삭제(loginStoreOwner)
            }
        }

        describe("PATCH /api/v1/store-owners/modify") {
            it("점주 정보를 수정한다") {
                val (token, loginCustomer) = 유저_생성_및_토큰_반환()

                val updateDto = StoreOwnerUpdateDto(nickname = Nickname("엄복동"))
                점주_정보_수정(token, updateDto)

                val customerDto = 점주_정보_조회_성공(token)
                customerDto.nickname shouldBe "엄복동"

                데이터_삭제(loginCustomer)
            }
        }

        describe("DELETE /api/v1/store-owners/sign-out") {
            it("점주 탈퇴에 성공한다") {
                val (token, loginCustomer) = 유저_생성_및_토큰_반환()

                점주_탈퇴(token)

                점주_정보_찾을_수_없음(token)

                데이터_삭제(loginCustomer)
            }
        }
    }

    private fun 유저_생성_및_토큰_반환(): Pair<String, LoginStoreOwner> {
        val account = userAccountService.createAccount(
            KakaoOAuth2(
                mapOf(
                    "id" to System.currentTimeMillis(),
                    "kakao_account" to mapOf(
                        "email" to "test${System.nanoTime()}@test.com",
                        "profile" to mapOf("nickname" to "홍길동")
                    )
                ),
                Oauth2Provider.KAKAO,
                "token",
                LocalDateTime.now().plusHours(1),
                "profile,email"
            )
        )

        val storeOwner = storeOwnerService.createByAccount(account)

        val loginStoreOwner = LoginStoreOwner(
            userAccountId = account.id,
            roles = account.roles.map { it.name },
            storeOwnerId = storeOwner.id,
            nickname = account.nickname?.value ?: "홍길동"
        )

        val token = tokenProvider.createAccessToken(loginStoreOwner)
        return token to loginStoreOwner
    }

    private fun 데이터_삭제(loginCustomer: LoginStoreOwner) {
        try {
            storeOwnerService.deleteStoreOwner(loginCustomer.storeOwnerId)
            userAccountService.deleteAccount(loginCustomer.userAccountId)
        } catch (e: Exception) {
            println("데이터 삭제 실패: ${e.message}")
        }
    }

    private fun 점주_정보_조회_성공(token: String): StoreOwnerDto {
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

        return response.jsonPath().getObject("data", StoreOwnerDto::class.java)
    }

    private fun 점주_정보_수정(token: String, updateDto: StoreOwnerUpdateDto) {
        RestAssured.given()
            .cookie("access_token", token)
            .contentType(ContentType.JSON)
            .body(updateDto)
            .`when`()
            .patch("/modify")
            .then()
            .statusCode(204)
    }

    private fun 점주_탈퇴(token: String) {
        RestAssured.given()
            .cookie("access_token", token)
            .`when`()
            .delete("/sign-out")
            .then()
            .statusCode(204)
    }

    private fun 점주_정보_찾을_수_없음(token: String) {
        RestAssured.given()
            .cookie("access_token", token)
            .`when`()
            .get("/me")
            .then()
            .statusCode(404)
    }
}