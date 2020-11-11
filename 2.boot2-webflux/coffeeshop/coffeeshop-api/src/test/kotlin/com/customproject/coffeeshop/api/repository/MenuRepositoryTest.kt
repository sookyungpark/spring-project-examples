package com.customproject.coffeeshop.api.repository

import com.customproject.coffeeshop.domain.Cost
import com.customproject.coffeeshop.domain.Currency
import com.customproject.coffeeshop.domain.Menu
import com.customproject.coffeeshop.domain.Recipe
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class MenuRepositoryTest {
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

    private val menuRepository: MenuRepository = MockMenuRepository()

    @Test
    fun list() {
        // when
        val actualResult = menuRepository.list().collectList().block()

        // then
        Assert.assertNotNull(actualResult)
    }

    @Test
    fun get() {
        val menuId = "id"

        // when
        val actualResult = menuRepository.get(menuId).block()

        // then
        Assert.assertNotNull(actualResult)
    }
}
