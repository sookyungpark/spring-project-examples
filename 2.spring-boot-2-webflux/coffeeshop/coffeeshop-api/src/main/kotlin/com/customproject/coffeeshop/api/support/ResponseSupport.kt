package com.customproject.coffeeshop.api.support

import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

class ResponseSupport {
    companion object {
        fun <T> drain(response: ClientResponse, convert: () -> T): Mono<T> {
            return response.bodyToMono<String>().defaultIfEmpty("")
                .map { convert() }
        }
    }
}
