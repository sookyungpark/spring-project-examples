package com.customproject.coffeeshop.api.handler.internal

import com.customproject.coffeeshop.api.exception.UnexpectedException
import com.customproject.coffeeshop.api.handler.BaseHandler
import com.customproject.coffeeshop.api.service.UserService
import com.customproject.coffeeshop.api.support.RequestLoggingSupport
import org.apache.commons.lang3.Range
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest

@Component
class InternalUserHandler(private val userService: UserService,
                          requestLoggingSupport: RequestLoggingSupport) : BaseHandler(requestLoggingSupport) {
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

}