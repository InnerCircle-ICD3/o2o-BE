package com.eatngo.postgresql.entity

import jakarta.persistence.*

@Entity
@Table(name = "sample")
data class SampleEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val name: String = ""
)