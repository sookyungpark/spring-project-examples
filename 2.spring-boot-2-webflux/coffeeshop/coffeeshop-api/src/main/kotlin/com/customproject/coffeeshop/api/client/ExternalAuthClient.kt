package com.customproject.coffeeshop.api.client

import com.customproject.coffeeshop.domain.request.external.ExternalAuthUserRequest
import com.customproject.coffeeshop.api.exception.UnauthorizedException
import com.customproject.coffeeshop.api.support.CoffeeshopConstants.Companion.HEADER_COFFEESHOP_REQUEST_ID
import com.customproject.coffeeshop.api.support.CoffeeshopConstants.Companion.ATTR_RESOURCE_ID_KEY
import com.customproject.coffeeshop.api.support.CoffeeshopConstants.Companion.ATTR_TRANSACTION_ID_KEY
import com.customproject.coffeeshop.api.support.ResponseSupport
import com.customproject.coffeeshop.domain.response.external.ExternalAuthUserResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class ExternalAuthClient(@Qualifier("authClient") private val authClient: WebClient) {

    @Throws(UnauthorizedException::class)
    fun authorize(userToken: String): Mono<ExternalAuthUserResponse> {
        val body = ExternalAuthUserRequest(userToken)

        return authClient
                .method(HttpMethod.POST)
                .uri("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(body))
                .attribute(ATTR_RESOURCE_ID_KEY, "authorizeUser")
                .exchange()
                .flatMap { response ->
                    if (response.statusCode().isError) {
                        return@flatMap ResponseSupport.drain(response) {
                            throw UnauthorizedException("user auth failed. ${response.statusCode().reasonPhrase}")
                        }
                    }
                    response.bodyToMono(ExternalAuthUserResponse::class.java)
                }
                .onErrorMap { UnauthorizedException("cannot authorize user", it) }
    }
}
