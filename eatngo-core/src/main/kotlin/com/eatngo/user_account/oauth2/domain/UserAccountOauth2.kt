package com.eatngo.user_account.oauth2.domain

import com.eatngo.user_account.domain.UserAccount
import com.eatngo.user_account.oauth2.constants.Oauth2Provider
import com.eatngo.user_account.oauth2.dto.Oauth2
import com.eatngo.user_account.vo.EmailAddress
import java.time.LocalDateTime

class UserAccountOauth2(
    val id: Long = 0,
    val userAccount: UserAccount,
    val email: EmailAddress?,
    val nickname: String? = null,
    val provider: Oauth2Provider,
    val userKey: String,
    val accessToken: String? = null,
    val expireAt: LocalDateTime? = null,
    val scopes: String? = null,
    val createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null,
    val deletedAt: LocalDateTime? = null
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
                email = oauth2.email.let { it?.let { EmailAddress(it) } },
                nickname = oauth2.nickname,
                provider = oauth2.provider,
                userKey = oauth2.principal,
            )
            val newTerms = oauth2.terms.map {
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