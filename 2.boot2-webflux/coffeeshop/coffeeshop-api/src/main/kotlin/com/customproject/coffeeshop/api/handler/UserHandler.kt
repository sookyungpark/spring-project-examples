package com.customproject.coffeeshop.api.handler

import com.customproject.coffeeshop.api.exception.UnexpectedException
import com.customproject.coffeeshop.api.service.UserService
import com.customproject.coffeeshop.api.support.RequestLoggingSupport
import org.apache.commons.lang3.Range
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest


@Component
public class UserHandler(private val userService: UserService,
                         requestLoggingSupport: RequestLoggingSupport) : BaseHandler(requestLoggingSupport) {
    @Throws(UnexpectedException::class)
    public fun getProfile(r: ServerRequest) = monoResponse(r) { request ->
        val userId = request.pathVariable("id")

        userService.getProfile(id = userId)
    }
}
