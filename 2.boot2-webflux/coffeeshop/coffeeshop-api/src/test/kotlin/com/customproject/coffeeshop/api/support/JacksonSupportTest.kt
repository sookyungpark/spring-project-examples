package com.customproject.coffeeshop.api.support

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.customproject.coffeeshop.domain.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class JacksonSupportTest {
    companion object {
        val objectMapper: ObjectMapper = ObjectMapper().apply {
            registerModules(AfterburnerModule(), JavaTimeModule())
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            registerModule(KotlinModule())
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }
    }

    @Test
    fun convertOrder() {
        val orderId = "orderId"
        val menus = listOf(OrderMenu("menuId", 1))
        val order = Order(orderId, menus)

        val expectedResult = hashMapOf<String, Any?>().apply {
            put("id", orderId)
            put("menus", menus)
        }

        // when
        val actualResult = JacksonSupport.convert(order, object : TypeReference<HashMap<String, Any>>() {})
        // then
        Assert.assertNotNull(actualResult)
        Assert.assertEquals(JacksonSupport.serialize(expectedResult), JacksonSupport.serialize(actualResult))
    }
}
