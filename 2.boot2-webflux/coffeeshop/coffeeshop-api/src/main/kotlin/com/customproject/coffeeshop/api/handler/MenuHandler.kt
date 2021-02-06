package com.customproject.coffeeshop.api.handler

import com.customproject.coffeeshop.api.domain.MenuSearchQuery
import com.customproject.coffeeshop.api.exception.UnexpectedException
import com.customproject.coffeeshop.api.service.MenuService
import com.customproject.coffeeshop.api.support.RequestLoggingSupport
import org.apache.commons.lang3.Range
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest


@Component
class MenuHandler(private val menuService: MenuService,
                  requestLoggingSupport: RequestLoggingSupport) : BaseHandler(requestLoggingSupport) {
    companion object {
        val LIST_COUNT_RANGE: Range<Long> = Range.between(1L, 20000L)
    }

    @Throws(UnexpectedException::class)
    fun list(r: ServerRequest) = fluxResponse(r) { request ->

        val countLimit = request.queryParam("limit")
                .map { it.toLong() }
                .filter { LIST_COUNT_RANGE.contains(it) }
                .orElse(LIST_COUNT_RANGE.maximum)

        menuService.list(countLimit = countLimit)
    }

    @Throws(UnexpectedException::class)
    fun get(r: ServerRequest) = monoResponse(r) { request ->
        menuService.get(id = request.pathVariable("id"))
    }

    @Throws(UnexpectedException::class)
    fun search(r: ServerRequest) = fluxResponse(r) { request ->
        request.bodyToMono(MenuSearchQuery::class.java)
                .flatMapMany { menuService.search(it) }
    }
}