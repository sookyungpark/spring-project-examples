package com.customproject.coffeeshop.api.client

import com.customproject.coffeeshop.domain.response.external.ExternalAuthUserResponse
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.nhaarman.mockito_kotlin.any
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono


@RunWith(MockitoJUnitRunner::class)
class ExternalAuthClientTest {
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

    private val authClient : WebClient = Mockito.mock(WebClient::class.java)
    private val authClientBody = Mockito.mock(WebClient.RequestBodyUriSpec::class.java)

    private val authServiceExternal: ExternalAuthClient = ExternalAuthClient(authClient)

    @Test
    fun authorizeWithProvider() {
        val userToken = "userToken"

        val expectedResult = ExternalAuthUserResponse(
            userKey = "user_key",
            createdTime = 0L,
            expireTime = 1000L,
            signedIn = true)

        val authResponse = ClientResponse
                .create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(expectedResult))
                .build()

        Mockito.`when`(authClient.method(HttpMethod.POST)).thenReturn(authClientBody)
        Mockito.`when`(authClientBody.uri(ArgumentMatchers.anyString())).thenReturn(authClientBody)
        Mockito.`when`(authClientBody.contentType(any())).thenReturn(authClientBody)
        Mockito.`when`(authClientBody.body(any())).thenReturn(authClientBody)
        Mockito.`when`(authClientBody.attribute(any(), any())).thenReturn(authClientBody)
        Mockito.`when`(authClientBody.exchange()).thenReturn(Mono.just(authResponse))

        // when
        val actualResult = authServiceExternal.authorize(userToken = userToken).block()

        // then
        Assert.assertNotNull(actualResult)
        Assert.assertEquals(expectedResult, actualResult)
    }
}
