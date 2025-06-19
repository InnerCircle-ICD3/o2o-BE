package com.eatngo.auth.handler

import com.eatngo.auth.dto.LoginStoreOwner
import com.eatngo.auth.dto.LoginUser
import com.eatngo.store.infra.StorePersistence
import com.eatngo.store_owner.domain.StoreOwner
import com.eatngo.store_owner.infra.StoreOwnerPersistence
import com.eatngo.user_account.domain.UserRole
import com.eatngo.user_account.infra.UserAccountPersistence
import com.eatngo.user_account.oauth2.constants.Role
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class StoreOwnerOAuth2SuccessPostProcessor(
    private val userAccountPersistence: UserAccountPersistence,
    private val storeOwnerPersistence: StoreOwnerPersistence,
    private val storePersistence: StorePersistence,
) : OAuth2SuccessPostProcessor {

    override fun postProcess(
        userId: Long,
        response: HttpServletResponse
    ): LoginUser {
        val storeOwner = storeOwnerPersistence.findByUserId(userId) ?: run {
            val userAccount = userAccountPersistence.getByIdOrThrow(userId)
            userAccount.roles.add(UserRole(null, Role.STORE_OWNER))
            userAccountPersistence.save(userAccount)
            storeOwnerPersistence.save(StoreOwner.create(userAccount))
        }

        val loginStoreOwner = LoginStoreOwner(
            userAccountId = userId,
            roles = listOf(Role.USER.name, Role.STORE_OWNER.name),
            nickname = storeOwner.account.nickname?.value,
            storeOwnerId = storeOwner.id
        )

        val authorities = loginStoreOwner.roles.map { SimpleGrantedAuthority(it) }
        val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(loginStoreOwner, null, authorities)
        SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken

        if (storePersistence.findByOwnerId(storeOwner.id).isEmpty()) {
            response.status = HttpServletResponse.SC_MOVED_TEMPORARILY
            response.setHeader("Location", "/store/register")
        }

        return loginStoreOwner
    }
}