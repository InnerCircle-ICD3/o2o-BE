package com.eatngo.postgresql.repository

import com.eatngo.postgresql.entity.SampleEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SampleRepository : JpaRepository<SampleEntity, Long>