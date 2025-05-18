package com.eatngo.order.rdb.entity.common

import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class SoftDeletableJpaEntity(

    var deletedAt: LocalDateTime?
)