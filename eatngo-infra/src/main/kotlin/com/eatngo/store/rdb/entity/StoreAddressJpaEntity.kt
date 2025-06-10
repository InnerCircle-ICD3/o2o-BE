package com.eatngo.store.rdb.entity

import com.eatngo.common.BaseJpaEntity
import com.eatngo.constants.DELETED_FILTER
import jakarta.persistence.*
import org.hibernate.annotations.Filter


@Entity
@Filter(name = DELETED_FILTER)
@Table(name = "store_address")
class AddressJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val roadNameAddress: String? = null,

    @Column(nullable = false)
    val lotNumberAddress: String,

    @Column
    val buildingName: String? = null,

    @Column(nullable = false)
    val zipCode: String,

    @Column
    val region1DepthName: String? = null,

    @Column
    val region2DepthName: String? = null,

    @Column
    val region3DepthName: String? = null,

    @Column(nullable = false)
    val latitude: Double,

    @Column(nullable = false)
    val longitude: Double
) : BaseJpaEntity()
