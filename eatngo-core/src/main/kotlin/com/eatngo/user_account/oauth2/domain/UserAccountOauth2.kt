package com.eatngo.user_account.oauth2.domain

import com.eatngo.user_account.domain.UserAccount
import com.eatngo.user_account.oauth2.constants.Oauth2Provider
import com.eatngo.user_account.oauth2.dto.Oauth2
import com.eatngo.user_account.vo.EmailAddress
import java.time.ZonedDateTime

class UserAccountOauth2(
    val id: Long = 0,
    val userAccount: UserAccount,
    val email: EmailAddress,
    val nickname: String?,
    val provider: Oauth2Provider,
    val principal: String,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime,
    val isDeleted: Boolean = false,
    val deletedAt: ZonedDateTime? = null,
) {

    private val _terms = mutableListOf<UserAccountOauth2Term>()
    val terms: List<UserAccountOauth2Term> get() = _terms

    fun addTerms(newTerms: List<UserAccountOauth2Term>) {
        _terms.addAll(newTerms)
    }

    companion object {
        fun of(
            account: UserAccount, oauth2: Oauth2
        ): UserAccountOauth2 {
            val userAccountOauth2 = UserAccountOauth2(
                userAccount = account,
                email = EmailAddress.from(oauth2.getEmail()),
                nickname = oauth2.getNickname(),
                provider = oauth2.getProvider(),
                principal = oauth2.getPrincipal(),
                createdAt = ZonedDateTime.now(),
                updatedAt = ZonedDateTime.now()
            )
            val newTerms = oauth2.getTerms().map {
                UserAccountOauth2Term.of(
                    userAccountOauth2 = userAccountOauth2,
                    tag = it.tag
                )
            }
            userAccountOauth2.addTerms(newTerms)
            return userAccountOauth2
        }
    }
}