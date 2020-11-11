package com.customproject.coffeeshop.api.handler

import com.customproject.coffeeshop.api.exception.UnauthorizedException
import com.customproject.coffeeshop.api.exception.UnexpectedException
import com.customproject.coffeeshop.api.service.OrderService
import com.customproject.coffeeshop.api.support.CoffeeshopConstants.Companion.ATTR_USERID_KEY
import com.customproject.coffeeshop.api.support.RequestLoggingSupport
import com.customproject.coffeeshop.domain.request.CreateOrderRequest
import com.customproject.coffeeshop.domain.request.UpdateOrderRequest
import org.apache.commons.lang3.Range
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse


@Component
public class OrderHandler(private val orderService: OrderService,
                          requestLoggingSupport: RequestLoggingSupport) : BaseHandler(requestLoggingSupport) {
    companion object {
        val LIST_COUNT_RANGE: Range<Long> = Range.between(1L, 20000L)
    }

    @Throws(UnexpectedException::class)
    public fun create(r: ServerRequest) = response(r) { request ->

        val requestBody = request.bodyToMono(CreateOrderRequest::class.java)
        val userId = request.attribute(ATTR_USERID_KEY)
                .map { it.toString() }
                .orElseThrow { UnauthorizedException("unauthorized user") }

        orderService.create(userId = userId, requestBody = requestBody)
                .flatMap { ServerResponse.noContent().build() }
    }

    @Throws(UnexpectedException::class)
    public fun update(r: ServerRequest) = response(r) { request ->

        val requestBody = request.bodyToMono(UpdateOrderRequest::class.java)
        val userId = request.attribute(ATTR_USERID_KEY)
                .map { it.toString() }
                .orElseThrow { UnauthorizedException("unauthorized user") }

        orderService.update(userId = userId, requestBody = requestBody)
                .flatMap { ServerResponse.noContent().build() }
    }
}
