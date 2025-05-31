package com.eatngo.resolver

import com.eatngo.auth.dto.LoginCustomer
import com.eatngo.auth.token.TokenProvider
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class TestCustomerIdArgumentResolver(
    private val tokenProvider: TokenProvider
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter) = true

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ) = run {
        val cookie = webRequest.getHeader("Cookie")
        cookie?.split(";")
            ?.find { it.trim().startsWith("access_token=") }
            ?.substringAfter("access_token=")
            .let { token ->
                if (!token.isNullOrBlank()) {
                    val loginCustomer = tokenProvider.getAuthentication(token).principal as LoginCustomer
                    loginCustomer.customerId
                } else {
                    null
                }
            }
    }
}