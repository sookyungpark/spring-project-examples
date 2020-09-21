package com.customproject.coffeeshop.api.support

import com.customproject.coffeeshop.api.exception.InvalidValueException
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
class DateTimeSupportTest {
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

    @Test
    fun generate() {
        val formatterPattern = Regex("\\d{14}")

        // when
        val actualResult = DateTimeSupport.generate(DateTimeSupport.FORMATTER_UUUUMMDDHHMMSS)
        // then
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(formatterPattern.matches(actualResult))
    }

    @Test
    fun validateTrue() {
        DateTimeSupport.validate("20191028182135", DateTimeSupport.FORMATTER_UUUUMMDDHHMMSS)
    }

    @Test(expected = InvalidValueException::class)
    fun validateFalse() {
        DateTimeSupport.validate("10021028182135", DateTimeSupport.FORMATTER_UUUUMMDDHHMMSS)
    }

    @Test
    fun serialize() {
        val time = 1572254902000L
        val expected = "20191028092822"

        val actual = DateTimeSupport.serialize(time, DateTimeSupport.FORMATTER_UUUUMMDDHHMMSS)
        // then
        Assert.assertNotNull(actual)
        Assert.assertEquals(expected, actual)
    }

    @Test
    @Throws(InvalidValueException::class)
    fun deserialize() {
        val time = "20191028092822"
        val expected = 1572254902000L

        val actual = DateTimeSupport.deserialize(time, DateTimeSupport.FORMATTER_UUUUMMDDHHMMSS)
        // then
        Assert.assertNotNull(actual)
        Assert.assertEquals(expected, actual)
    }
}