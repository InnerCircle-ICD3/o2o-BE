package com.eatngo.common.converter

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.persistence.AttributeConverter

abstract class JsonAttributeConverter<T>(private val clazz: Class<T>) : AttributeConverter<T, String> {
    companion object {
        private val objectMapper = jacksonObjectMapper().apply {
            // 줄바꿈, 들여쓰기 없이 한 줄로만 직렬화
            disable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT)
        }
    }
    override fun convertToDatabaseColumn(attribute: T?): String? =
        attribute?.let { objectMapper.writeValueAsString(it) }
    override fun convertToEntityAttribute(dbData: String?): T? =
        dbData?.let { objectMapper.readValue(it, clazz) }
}

abstract class JsonListAttributeConverter<T>(private val typeRef: TypeReference<T>) : AttributeConverter<T, String> {
    companion object {
        private val objectMapper = jacksonObjectMapper().apply {
            // 줄바꿈, 들여쓰기 없이 한 줄로만 직렬화
            disable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT)
        }
    }
    override fun convertToDatabaseColumn(attribute: T?): String? =
        attribute?.let { objectMapper.writeValueAsString(it) }
    override fun convertToEntityAttribute(dbData: String?): T? =
        dbData?.let { objectMapper.readValue(it, typeRef) }
}
