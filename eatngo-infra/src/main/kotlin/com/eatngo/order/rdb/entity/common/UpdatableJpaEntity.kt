package com.eatngo.order.rdb.entity.common

import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class UpdatableJpaEntity(
    @LastModifiedBy
    var updatedBy: String?,

    @LastModifiedDate
    var updatedAt: LocalDateTime?
)