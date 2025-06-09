package com.eatngo.helper

import com.eatngo.auth.dto.LoginCustomer
import com.eatngo.auth.token.TokenProvider
import com.eatngo.customer.service.CustomerAddressService
import com.eatngo.customer.service.CustomerService
import com.eatngo.user_account.oauth2.constants.Oauth2Provider
import com.eatngo.user_account.oauth2.dto.KakaoOAuth2
import com.eatngo.user_account.service.UserAccountService
import org.springframework.boot.test.context.TestComponent

@TestComponent
class CustomerTestHelper(
    private val userAccountService: UserAccountService,
    private val customerService: CustomerService,
    private val customerAddressService: CustomerAddressService,
    private val tokenProvider: TokenProvider,
) {
    fun 유저_생성_및_토큰_반환(): Pair<String, LoginCustomer> {
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
                expiresAt = java.time.LocalDateTime.now().plusDays(1),
                scopes = "profile,email"

            )
        )

        val customer = customerService.createByAccount(account)

        val loginCustomer = LoginCustomer(
            userAccountId = account.id,
            roles = account.roles.map { it.role.name },
            customerId = customer.id,
            nickname = customer.account.nickname?.value ?: "홍길동"
        )

        val token = tokenProvider.createAccessToken(loginCustomer)
        return token to loginCustomer
    }

    fun 데이터_삭제(loginCustomer: LoginCustomer) {
        try {
            customerService.deleteCustomer(loginCustomer.customerId)
            userAccountService.deleteAccount(loginCustomer.userAccountId)
            customerAddressService.getAddressList(loginCustomer.customerId)
                .forEach { customerAddressService.deleteAddress(it.customerId, it.id) }
        } catch (e: Exception) {
            println("데이터 삭제 실패: ${e.message}")
        }
    }
}