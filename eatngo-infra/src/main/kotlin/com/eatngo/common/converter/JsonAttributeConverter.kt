package com.eatngo.common.converter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.persistence.AttributeConverter

abstract class JsonAttributeConverter<T>(private val clazz: Class<T>) : AttributeConverter<T, String> {
    private val objectMapper = jacksonObjectMapper()

    override fun convertToDatabaseColumn(attribute: T?): String? =
        attribute?.let { objectMapper.writeValueAsString(it) }

    override fun convertToEntityAttribute(dbData: String?): T? =
        dbData?.let { objectMapper.readValue(it, clazz) }
}
