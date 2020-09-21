package com.customproject.coffeeshop.api.filter

import com.customproject.coffeeshop.api.client.ExternalAuthClient
import com.customproject.coffeeshop.api.support.CoffeeshopConstants
import com.customproject.coffeeshop.api.support.CoffeeshopConstants.Companion.ATTR_USERID_KEY
import com.customproject.coffeeshop.api.support.FilterOrder
import org.springframework.core.Ordered
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.HandlerFilterFunction
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono


@Component
class UserAuthCheckFilter(private val externalAuthClient: ExternalAuthClient) : HandlerFilterFunction<ServerResponse, ServerResponse>, Ordered {

    override fun getOrder(): Int {
        return FilterOrder.USER_AUTH_CHECK_FILTER.value
    }

    override fun filter(request: ServerRequest, next: HandlerFunction<ServerResponse>): Mono<ServerResponse> {
        val userToken = request.headers().header(CoffeeshopConstants.HEADER_COFFEESHOP_USER_TOKEN).firstOrNull()
                ?: return ServerResponse.status(HttpStatus.UNAUTHORIZED).build()

        return externalAuthClient.authorize(userToken)
                .flatMap {
                    request.attributes()[ATTR_USERID_KEY] = it.userKey
                    next.handle(request)
                }
                .onErrorResume { ServerResponse.status(HttpStatus.UNAUTHORIZED).build() }
    }
}