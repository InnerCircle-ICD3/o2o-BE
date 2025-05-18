package com.eatngo.common

import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class CreatableJpaEntity(

    @CreatedBy
    var createdBy: Long? = null,

    @CreatedDate
    var createdAt: LocalDateTime? = null
)