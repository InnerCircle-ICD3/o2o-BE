package com.eatngo.user_account.persistence

import com.eatngo.aop.SoftDeletedFilter
import com.eatngo.user_account.domain.UserAccount
import com.eatngo.user_account.infra.UserAccountPersistence
import com.eatngo.user_account.oauth2.constants.Oauth2Provider
import com.eatngo.user_account.rdb.entity.UserAccountJpaEntity
import com.eatngo.user_account.rdb.repository.UserAccountRdbRepository
import com.eatngo.user_account.vo.EmailAddress
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class UserAccountPersistenceImpl(
    private val userAccountRdbRepository: UserAccountRdbRepository,
) : UserAccountPersistence {

    override fun save(account: UserAccount): UserAccount {
        val from = UserAccountJpaEntity.from(account)
        val accountJpaEntity = userAccountRdbRepository.save(from)
        return UserAccountJpaEntity.toUserAccount(accountJpaEntity)
    }

    @SoftDeletedFilter
    override fun findById(id: Long): UserAccount? {
        return userAccountRdbRepository.findById(id)
            .orElse(null)
            ?.let { UserAccountJpaEntity.toUserAccount(it) }
    }

    @SoftDeletedFilter
    override fun deleteById(id: Long) {
        userAccountRdbRepository.findById(id).orElseThrow()
            .apply { delete() }
    }

    @SoftDeletedFilter
    override fun findByOauth(userKey: String, provider: Oauth2Provider) =
        userAccountRdbRepository.findByOAuth2Key(userKey, provider)
            ?.let { UserAccountJpaEntity.toUserAccount(it) }

    @SoftDeletedFilter
    override fun findByEmail(emailAddress: EmailAddress): UserAccount? {
        return userAccountRdbRepository.findByEmail(emailAddress.value)
            ?.let { UserAccountJpaEntity.toUserAccount(it) }
    }
}


