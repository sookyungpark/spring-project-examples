package com.customproject.coffeeshop.api.support

import org.springframework.http.MediaType
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class ResponseSupport {
    companion object {
        fun <T> drain(response: ClientResponse, convert: () -> T): Mono<T> {
            return response.bodyToMono<String>().defaultIfEmpty("")
                    .map { convert() }
        }

        @Throws(Exception::class)
        public inline fun <reified T> monoResponse(request: ServerRequest, func: (ServerRequest) -> Mono<T>): Mono<ServerResponse> {
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(func.invoke(request), T::class.java)
        }

        @Throws(Exception::class)
        public inline fun <reified T> fluxResponse(request: ServerRequest, func: (ServerRequest) -> Flux<T>): Mono<ServerResponse> {
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(func.invoke(request), T::class.java)
        }
    }
}
