package com.eatngo.common

import com.eatngo.constants.DELETED_FILTER
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.FilterDef
import java.time.LocalDateTime

@FilterDef(
    name = DELETED_FILTER,
    defaultCondition = "deleted_at is null",
)
@MappedSuperclass
abstract class SoftDeletableJpaEntity(
    var deletedAt: LocalDateTime? = null,
) {
    open fun delete() {
        this.deletedAt = LocalDateTime.now()
    }
}