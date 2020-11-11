package com.customproject.coffeeshop.api.handler

import com.customproject.coffeeshop.api.exception.UnexpectedException
import com.customproject.coffeeshop.api.service.UserService
import com.customproject.coffeeshop.api.support.ResponseSupport.Companion.fluxResponse
import com.customproject.coffeeshop.api.support.ResponseSupport.Companion.monoResponse
import org.apache.commons.lang3.Range
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest


@Component
public class UserHandler(private val userService: UserService) {
    companion object {
        val LIST_COUNT_RANGE: Range<Long> = Range.between(1L, 20000L)
    }

    @Throws(UnexpectedException::class)
    public fun list(r: ServerRequest) = fluxResponse(r) { request ->

        val countLimit = request.queryParam("limit")
                .map { it.toLong() }
                .filter { LIST_COUNT_RANGE.contains(it) }
                .orElse(LIST_COUNT_RANGE.maximum)

        userService.list(countLimit = countLimit)
    }

    @Throws(UnexpectedException::class)
    public fun getProfile(r: ServerRequest) = monoResponse(r) { request ->

        val userId = request.pathVariable("id")
        userService.getProfile(id = userId)
    }
}
