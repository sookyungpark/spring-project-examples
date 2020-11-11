package com.customproject.coffeeshop.api.handler

import com.customproject.coffeeshop.api.exception.UnauthorizedException
import com.customproject.coffeeshop.api.exception.UnexpectedException
import com.customproject.coffeeshop.api.service.OrderService
import com.customproject.coffeeshop.api.support.CoffeeshopConstants.Companion.ATTR_USERID_KEY
import com.customproject.coffeeshop.api.support.ResponseSupport.Companion.monoResponse
import com.customproject.coffeeshop.domain.request.CreateOrderRequest
import com.customproject.coffeeshop.domain.request.UpdateOrderRequest
import org.apache.commons.lang3.Range
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest


@Component
public class OrderHandler(private val orderService: OrderService) {
    companion object {
        val LIST_COUNT_RANGE: Range<Long> = Range.between(1L, 20000L)
    }

    @Throws(UnexpectedException::class)
    public fun create(r: ServerRequest) = monoResponse(r) { request ->

        val requestBody = request.bodyToMono(CreateOrderRequest::class.java)
        val userId = request.attribute(ATTR_USERID_KEY)
                .map { it.toString() }
                .orElseThrow { UnauthorizedException("unauthorized user") }

        orderService.create(userId = userId, requestBody = requestBody)
    }

    @Throws(UnexpectedException::class)
    public fun update(r: ServerRequest) = monoResponse(r) { request ->

        val requestBody = request.bodyToMono(UpdateOrderRequest::class.java)
        val userId = request.attribute(ATTR_USERID_KEY)
                .map { it.toString() }
                .orElseThrow { UnauthorizedException("unauthorized user") }

        orderService.update(userId = userId, requestBody = requestBody)
    }
}
