package com.customproject.coffeeshop.api.handler

import io.micrometer.prometheus.PrometheusMeterRegistry
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono


@Component
public class ManagementHandler(private val prometheusMeterRegistry: PrometheusMeterRegistry) {
    public fun scrape(request: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody(prometheusMeterRegistry.scrape())
    }
}