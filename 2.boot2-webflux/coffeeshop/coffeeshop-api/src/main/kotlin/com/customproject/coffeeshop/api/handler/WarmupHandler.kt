package com.customproject.coffeeshop.api.handler

import com.customproject.coffeeshop.api.service.WarmupService
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono


@Component
public class WarmupHandler(private val warmupService: WarmupService) {
    public fun warmup(request: ServerRequest): Mono<ServerResponse> {
        return warmupService.warmup().let {
            ServerResponse.noContent().build()
        }
    }
}
