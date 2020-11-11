package com.customproject.coffeeshop.api.filter

import com.customproject.coffeeshop.api.exception.CoffeeshopException
import com.customproject.coffeeshop.api.support.CoffeeshopConstants.Companion.IP_HEADER_CANDIDATES
import com.customproject.coffeeshop.api.support.CoffeeshopConstants.Companion.TAG_VALUE_UNKNOWN_RESOURCE
import com.customproject.coffeeshop.api.support.FilterOrder
import com.customproject.coffeeshop.domain.ServerExchangeCode
import com.customproject.coffeeshop.domain.ServerExchangeLog
import mu.KotlinLogging
import org.apache.http.HttpStatus
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.route.Route
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils
import org.springframework.core.Ordered
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.core.publisher.Signal


@Component
public class ProxyRequestLoggingFilter : GatewayFilter, Ordered {

    companion object {
        private const val DEFAULT_SEPARATOR = "::"

        private val requestLogger = KotlinLogging.logger("proxy-request-logger")
    }

    override fun filter(exchange: ServerWebExchange?, chain: GatewayFilterChain): Mono<Void> {
        return chain.filter(exchange)
                .doOnEach {
                    if (!it.isOnComplete) {
                        return@doOnEach
                    }
                    if (it?.isOnError == true) {
                        error(exchange, it)
                        return@doOnEach
                    }
                    success(exchange, it)
                }
    }

    override fun getOrder(): Int {
        return FilterOrder.REQUEST_LOGGING_FILTER.value
    }

    fun success(exchange: ServerWebExchange?, signal: Signal<Void?>?) {
        exchange?: return
        try {
            // log request
            logSuccessRequest(exchange)
        } catch (e: Exception) {
            // do nothing
        }
    }


    fun error(exchange: ServerWebExchange?, signal: Signal<Void?>?) {
        exchange ?: return
        try {
            logFailureRequest(exchange, signal)
        } catch (e: Exception) {
            // do nothing
        }
    }

    private fun logSuccessRequest(exchange: ServerWebExchange) {
        ServerExchangeLog(
                code = getCode(exchange),
                uri = exchange.request.path.value(),
                method = exchange.request.methodValue,
                clientIp = getClientIp(exchange.request),
                statusCode = exchange.response.statusCode?.value() ?: HttpStatus.SC_INTERNAL_SERVER_ERROR).let {
            requestLogger.info { it.toString(DEFAULT_SEPARATOR) }
        }
    }

    private fun logFailureRequest(exchange: ServerWebExchange, signal: Signal<Void?>?) {
        try {
            var statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR

            val throwable = signal?.throwable
            if (throwable is CoffeeshopException) {
                statusCode = throwable.webStatus.value()
            }

            ServerExchangeLog(
                    code = ServerExchangeCode.FAILURE,
                    uri = exchange.request.path.value(),
                    method = exchange.request.methodValue,
                    clientIp = getClientIp(exchange.request),
                    statusCode = statusCode
            ).let {
                requestLogger.info { it.toString(DEFAULT_SEPARATOR) }
            }
        } catch (e: Exception) {
            // do nothing
        }
    }

    private fun getCode(exchange: ServerWebExchange): ServerExchangeCode {
        exchange.response.statusCode?: return ServerExchangeCode.FAILURE
        if (exchange.response.statusCode!!.isError) {
            return ServerExchangeCode.FAILURE
        }
        return ServerExchangeCode.SUCCESS
    }

    private fun getClientIp(request: ServerHttpRequest): String? {
        try {
            for (header in IP_HEADER_CANDIDATES) {
                val requestHeader = request.headers[header]?: emptyList()
                if (requestHeader.isNotEmpty()) {
                    return requestHeader.joinToString()
                }
            }
        } catch (e: Exception) { }
        return null
    }
}
