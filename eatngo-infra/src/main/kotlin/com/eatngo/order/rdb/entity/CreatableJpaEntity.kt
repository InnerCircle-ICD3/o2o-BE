package com.eatngo.order.rdb.entity

import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.ZonedDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class CreatableJpaEntity(
    @CreatedBy
    var createdBy: String? = null,

    @CreatedDate
    var createdAt: ZonedDateTime? = null
)