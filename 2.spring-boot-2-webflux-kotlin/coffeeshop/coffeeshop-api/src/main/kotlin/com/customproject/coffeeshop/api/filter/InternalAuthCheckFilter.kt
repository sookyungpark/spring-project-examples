package com.customproject.coffeeshop.api.filter

import com.customproject.coffeeshop.api.support.FilterOrder
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.HandlerFilterFunction
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono

@Component
class InternalAuthCheckFilter : HandlerFilterFunction<ServerResponse, ServerResponse>, Ordered {

    override fun getOrder(): Int {
        return FilterOrder.INTERNAL_AUTH_CHECK_FILTER.value
    }

    override fun filter(request: ServerRequest, next: HandlerFunction<ServerResponse>): Mono<ServerResponse> {
        // TODO
        return next.handle(request)
    }
}
