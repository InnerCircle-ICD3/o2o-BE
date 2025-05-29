package com.eatngo.store.rdb.entity

import com.eatngo.common.BaseJpaEntity
import com.eatngo.constants.DELETED_FILTER
import com.eatngo.store.vo.BusinessHourVO
import jakarta.persistence.*
import org.hibernate.annotations.Filter
import java.time.DayOfWeek
import java.time.LocalTime

@Entity
@Table(name = "store_business_hours")
@Filter(name = DELETED_FILTER)
class BusinessHourJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    val store: StoreJpaEntity,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val dayOfWeek: DayOfWeek,

    @Column(nullable = false)
    val openTime: LocalTime,

    @Column(nullable = false)
    val closeTime: LocalTime
) : BaseJpaEntity() {
    companion object {
        fun of(vo: BusinessHourVO, store: StoreJpaEntity) = BusinessHourJpaEntity(
            store = store,
            dayOfWeek = vo.dayOfWeek,
            openTime = vo.openTime,
            closeTime = vo.closeTime
        )

        fun toVO(entity: BusinessHourJpaEntity) = BusinessHourVO.from(
            entity.dayOfWeek,
            entity.openTime,
            entity.closeTime
        )
    }
} 