package com.eatngo.common.converter

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.persistence.AttributeConverter

abstract class JsonAttributeConverter<T>(private val clazz: Class<T>) : AttributeConverter<T, String> {
    companion object {
        private val objectMapper = jacksonObjectMapper()
    }

    override fun convertToDatabaseColumn(attribute: T?): String? =
        attribute?.let {
            try {
                objectMapper.writeValueAsString(it)
            } catch (e: JsonProcessingException) {
                throw IllegalArgumentException("JSON 직렬화 실패: ${e.message}", e)
            }
        }

    override fun convertToEntityAttribute(dbData: String?): T? =
        dbData?.let {
            try {
                objectMapper.readValue(it, clazz)
            } catch (e: Exception) {
                throw IllegalArgumentException("JSON 역직렬화 실패: ${e.message}", e)
            }
        }
}
