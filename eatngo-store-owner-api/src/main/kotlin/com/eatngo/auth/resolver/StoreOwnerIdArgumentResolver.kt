package com.eatngo.auth.resolver

import com.eatngo.auth.annotaion.StoreOwnerId
import com.eatngo.auth.dto.LoginStoreOwner
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class StoreOwnerIdArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(StoreOwnerId::class.java)
                && parameter.parameterType == Long::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication?.principal

        return when (principal) {
            is String -> if (principal == "anonymousUser") null else principal.toLongOrNull()
            is LoginStoreOwner -> principal.storeOwnerId
            else -> null
        }
    }
}