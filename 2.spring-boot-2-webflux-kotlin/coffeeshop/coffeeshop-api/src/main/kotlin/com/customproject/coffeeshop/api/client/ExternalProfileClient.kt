package com.customproject.coffeeshop.api.client

import com.customproject.coffeeshop.api.exception.UnauthorizedException
import com.customproject.coffeeshop.api.support.CoffeeshopConstants
import com.customproject.coffeeshop.api.support.ResponseSupport
import com.customproject.coffeeshop.domain.response.external.ExternalProfileGetResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class ExternalProfileClient(@Qualifier("profileClient") private val profileClient: WebClient) {

    @Throws(UnauthorizedException::class)
    fun get(userId: String): Mono<ExternalProfileGetResponse> {

        return profileClient
                .method(HttpMethod.GET)
                .uri("/api/profile/$userId")
                .contentType(MediaType.APPLICATION_JSON)
                .attribute(CoffeeshopConstants.ATTR_RESOURCE_ID_KEY, "getProfile")
                .exchange()
                .flatMap { response ->
                    if (response.statusCode().isError) {
                        return@flatMap ResponseSupport.drain(response) {
                            throw UnauthorizedException("get profile failed. ${response.statusCode().reasonPhrase}")
                        }
                    }
                    response.bodyToMono(ExternalProfileGetResponse::class.java)
                }
                .onErrorMap { UnauthorizedException("get profile user", it) }
    }
}
