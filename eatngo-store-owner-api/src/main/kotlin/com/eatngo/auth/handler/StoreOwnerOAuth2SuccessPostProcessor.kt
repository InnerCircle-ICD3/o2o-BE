package com.eatngo.auth.handler

import com.eatngo.auth.dto.LoginStoreOwner
import com.eatngo.auth.dto.LoginUser
import com.eatngo.store_owner.domain.StoreOwner
import com.eatngo.store_owner.infra.StoreOwnerPersistence
import com.eatngo.user_account.infra.UserAccountPersistence
import com.eatngo.user_account.oauth2.constants.Role
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class StoreOwnerOAuth2SuccessPostProcessor(
    private val userAccountPersistence: UserAccountPersistence,
    private val storeOwnerPersistence: StoreOwnerPersistence,
) : OAuth2SuccessPostProcessor {

    override fun postProcess(userId: Long): LoginUser {
        val storeOwner = storeOwnerPersistence.findByUserId(userId) ?: run {
            val userAccount = userAccountPersistence.getByIdOrThrow(userId)
            storeOwnerPersistence.save(StoreOwner.create(userAccount))
        }

        val loginStoreOwner = LoginStoreOwner(
            userAccountId = userId,
            roles = listOf(Role.USER.name, Role.STORE_OWNER.name),
            storeOwnerId = storeOwner.id
        )

        val authorities = loginStoreOwner.roles.map { SimpleGrantedAuthority(it) }
        val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(loginStoreOwner, null, authorities)
        SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
        return loginStoreOwner
    }
}