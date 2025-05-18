package com.eatngo.user_account.rdb.repository

import com.eatngo.user_account.oauth2.constants.Oauth2Provider
import com.eatngo.user_account.rdb.entity.UserAccountJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface UserAccountRdbRepository : JpaRepository<UserAccountJpaEntity, Long> {

    @Query(
        """
        SELECT u FROM UserAccountJpaEntity u
        WHERE u.id = :id
    """
    )
    override fun findById(id: Long): Optional<UserAccountJpaEntity>

    @Query(
        """
        SELECT u FROM UserAccountJpaEntity u
        JOIN UserAccountOAuth2JpaEntity o2 on o2.provider = :provider
        and o2.userKey = :userKey
    """
    )
    fun findByOAuth2Key(userKey: String, provider: Oauth2Provider): UserAccountJpaEntity?

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
        UPDATE UserAccountJpaEntity u
        SET u.deletedAt = CURRENT_TIMESTAMP
        WHERE u.id = :id
    """
    )
    fun softDeleteById(id: Long)
}