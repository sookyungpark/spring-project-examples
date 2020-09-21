package com.customproject.coffeeshop.api.support

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class JacksonSupport {
    companion object {
        private val mapper = jacksonObjectMapper().apply {
            registerModules(AfterburnerModule(), JavaTimeModule())
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }

        fun <T> serialize(item: T): String {
            if (item is String) {
                return item
            }
            return mapper.writeValueAsString(item)
        }

        fun <T> deserialize(item: String, clazz: Class<T>): T {
            if (item.javaClass == clazz) {
                return item as T
            }
            return mapper.readValue(item, clazz)
        }

        fun <T> convert(item: Any, typeReference: TypeReference<T>): T {
            return mapper.convertValue(item, typeReference)
        }
    }
}