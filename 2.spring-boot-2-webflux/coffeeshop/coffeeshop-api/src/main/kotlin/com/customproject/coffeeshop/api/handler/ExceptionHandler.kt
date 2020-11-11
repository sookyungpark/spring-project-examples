package com.customproject.coffeeshop.api.handler

import com.customproject.coffeeshop.api.exception.CoffeeshopException
import com.customproject.coffeeshop.api.exception.HandlerNotFoundException
import com.customproject.coffeeshop.api.support.CoffeeshopConstants.Companion.HEADER_COFFEESHOP_ERROR_KEY
import com.customproject.coffeeshop.api.support.RequestLoggingSupport
import com.customproject.coffeeshop.domain.ErrorResponse
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.web.ResourceProperties
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Order(-2)
@Component
public class ExceptionHandler(serverCodecConfigurer: ServerCodecConfigurer,
                              errorAttributes: ErrorAttributes,
                              resourceProperties: ResourceProperties,
                              applicationContext: ApplicationContext,
                              private val requestLoggingSupport: RequestLoggingSupport)
        : AbstractErrorWebExceptionHandler(errorAttributes, resourceProperties, applicationContext) {

    companion object {
        private const val ATTRIBUTE_MESSAGE_KEY = "message"
        private const val NOT_FOUND_RESOURCE_ID = "notFound"

        private const val NO_MATCHING_HANDLER_MESSAGE = "No matching handler"
        private const val NO_MATCHING_HANDLER_CODE = "-1"

        private const val INTERNAL_ERROR_MESSAGE = "Internal Server Error"
        private val INTERNAL_ERROR_STATUS = HttpStatus.INTERNAL_SERVER_ERROR.value()

        private val log = KotlinLogging.logger {}
    }

    init {
        super.setMessageWriters(serverCodecConfigurer.writers)
    }

    override fun getRoutingFunction(errorAttributes: ErrorAttributes?): RouterFunction<ServerResponse> {
        return RouterFunctions.route(RequestPredicates.all(), HandlerFunction<ServerResponse>(this::renderErrorResponse))
    }

    protected fun renderErrorResponse(request: ServerRequest) : Mono<ServerResponse> {
        val t = getError(request)
        log.error(t) { "exception" }

        if (t is CoffeeshopException) {
            return ServerResponse.status(t.webStatus)
                .header(HEADER_COFFEESHOP_ERROR_KEY, t.errorStatus)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(
                        Mono.just(ErrorResponse(t.webStatus.value(), t.errorStatus)),
                        ErrorResponse::class.java
                )
        }

        val headers = hashMapOf<String, String>()

        if (isNoMatchingHandler(request)) {
            headers[HEADER_COFFEESHOP_ERROR_KEY] = NO_MATCHING_HANDLER_CODE
            requestLoggingSupport.error(request, NOT_FOUND_RESOURCE_ID, null, HandlerNotFoundException("handler not found"))
        }

        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .headers {
                it.setAll(headers)
            }
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .body(
                    Mono.just(ErrorResponse(INTERNAL_ERROR_STATUS, INTERNAL_ERROR_MESSAGE)),
                    ErrorResponse::class.java
            )
    }

    private fun isNoMatchingHandler(request: ServerRequest): Boolean {
        getErrorAttributes(request, false)[ATTRIBUTE_MESSAGE_KEY]?.let {
            if (it == NO_MATCHING_HANDLER_MESSAGE) {
                return true
            }
        }
        return false
    }
}
