package com.customproject.coffeeshop.api

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.KotlinModule

class ApiTestSupport {
    companion object {
        const val PHASE = "TEST"

        val objectMapper: ObjectMapper = ObjectMapper().apply {
            registerModules(AfterburnerModule(), JavaTimeModule())
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            registerModule(KotlinModule())
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }
    }
}