package com.eatngo.order.rdb.entity

import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.ZonedDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseJpaEntity(
    @CreatedBy
    var createdBy: String? = null,

    @CreatedDate
    var createdAt: ZonedDateTime? = null,

    @LastModifiedBy
    var updatedBy: String? = null,

    @LastModifiedDate
    var updatedAt: ZonedDateTime? = null,

    var deletedAt: ZonedDateTime? = null,
)