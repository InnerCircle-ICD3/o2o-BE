package com.eatngo.user_account.rdb.repository

import com.eatngo.user_account.oauth2.constants.OAuth2Provider
import com.eatngo.user_account.rdb.entity.UserAccountJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface UserAccountRdbRepository : JpaRepository<UserAccountJpaEntity, Long> {

    @Query(
        """
        SELECT u FROM UserAccountJpaEntity u
        JOIN fetch UserAccountOAuth2JpaEntity o2 ON o2.userAccount = u  
        JOIN fetch UserAccountRoleJpaEntity r ON r.account = u
        WHERE u.id = :id
    """
    )
    override fun findById(id: Long): Optional<UserAccountJpaEntity>

    @Query(
        """
        SELECT u FROM UserAccountJpaEntity u
        JOIN fetch UserAccountOAuth2JpaEntity o2 ON o2.userAccount = u  
        JOIN fetch UserAccountRoleJpaEntity r ON r.account = u
        WHERE o2.provider = :provider
        AND o2.userKey = :userKey
        order by u.id desc limit 1
    """
    )
    fun findByOAuth2Key(userKey: String, provider: OAuth2Provider): UserAccountJpaEntity?

    @Query(
        """
        SELECT u FROM UserAccountJpaEntity u
        JOIN fetch UserAccountOAuth2JpaEntity o2 ON o2.userAccount = u  
        JOIN fetch UserAccountRoleJpaEntity r ON r.account = u
        WHERE u.email = :email
    """
    )
    fun findByEmail(email: String): UserAccountJpaEntity?

}