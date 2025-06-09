package com.eatngo.auth.handler

import com.eatngo.auth.dto.LoginCustomer
import com.eatngo.auth.dto.LoginUser
import com.eatngo.customer.domain.Customer
import com.eatngo.customer.infra.CustomerPersistence
import com.eatngo.user_account.infra.UserAccountPersistence
import com.eatngo.user_account.oauth2.constants.Role
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class CustomerOAuth2SuccessPostProcessor(
    private val userAccountPersistence: UserAccountPersistence,
    private val customerPersistence: CustomerPersistence,
) : OAuth2SuccessPostProcessor {

    override fun postProcess(
        userId: Long,
        response: HttpServletResponse
    ): LoginUser {
        val customer = customerPersistence.findByUserId(userId) ?: run {
            val userAccount = userAccountPersistence.getByIdOrThrow(userId)
            customerPersistence.save(Customer.create(userAccount))
        }

        val loginCustomer = LoginCustomer(
            userAccountId = userId,
            roles = listOf(Role.USER.name, Role.CUSTOMER.name),
            nickname = customer.account.nickname?.value,
            customerId = customer.id
        )

        val authorities = loginCustomer.roles.map { SimpleGrantedAuthority(it) }
        val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(loginCustomer, null, authorities)
        SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken

        if (loginCustomer.nickname == null) {
            response.status = HttpServletResponse.SC_MOVED_TEMPORARILY
            response.setHeader("Location", "/mypage/complete-profile")
        }

        return loginCustomer
    }
}