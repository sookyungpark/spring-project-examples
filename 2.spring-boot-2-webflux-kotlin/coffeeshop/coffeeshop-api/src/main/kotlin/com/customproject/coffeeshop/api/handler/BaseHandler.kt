package com.customproject.coffeeshop.api.handler

import com.customproject.coffeeshop.api.support.CoffeeshopConstants.Companion.ATTR_RESOURCE_ID_KEY
import com.customproject.coffeeshop.api.support.CoffeeshopConstants.Companion.ATTR_START_TIME_KEY
import com.customproject.coffeeshop.api.support.CoffeeshopConstants.Companion.TAG_VALUE_UNKNOWN_RESOURCE
import com.customproject.coffeeshop.api.support.RequestLoggingSupport
import org.springframework.http.MediaType
import org.springframework.web.reactive.HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.CorePublisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


open class BaseHandler(private val requestLoggingSupport: RequestLoggingSupport) {

    @Throws(Exception::class)
    public inline fun response(request: ServerRequest, func: (ServerRequest) -> Mono<ServerResponse>): Mono<ServerResponse> {
        // log start time and resource id
        request.attributes()[ATTR_START_TIME_KEY] = System.currentTimeMillis()
        request.attributes()[ATTR_RESOURCE_ID_KEY] = request.attribute(BEST_MATCHING_PATTERN_ATTRIBUTE)
            .map { it.toString() }
            .orElse(TAG_VALUE_UNKNOWN_RESOURCE)

        return try {
            func.invoke(request)
                .doOnSuccess { logSuccess(request) }
                .doOnError { e -> logFailure(request, e) }

        } catch(e: Exception) {
            logFailure(request, e)
            throw e
        }
    }

    @Throws(Exception::class)
    inline fun <reified T> monoResponse(request: ServerRequest, func: (ServerRequest) -> Mono<T>): Mono<ServerResponse> {
        // log start time and resource id
        request.attributes()[ATTR_START_TIME_KEY] = System.currentTimeMillis()
        request.attributes()[ATTR_RESOURCE_ID_KEY] = request.attribute(BEST_MATCHING_PATTERN_ATTRIBUTE)
            .map { it.toString() }
            .orElse(TAG_VALUE_UNKNOWN_RESOURCE)

        val result: Mono<T> = try {
            func.invoke(request)
                .doOnSuccess { logSuccess(request) }
                .doOnError { e -> logFailure(request, e) }

        } catch(e: Exception) {
            logFailure(request, e)
            throw e
        }

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result, T::class.java)
    }

    @Throws(Exception::class)
    public inline fun <reified T> fluxResponse(request: ServerRequest, func: (ServerRequest) -> Flux<T>): Mono<ServerResponse> {
        // log start time and resource id
        request.attributes()[ATTR_START_TIME_KEY] = System.currentTimeMillis()
        request.attributes()[ATTR_RESOURCE_ID_KEY] = request.attribute(BEST_MATCHING_PATTERN_ATTRIBUTE)
            .map { it.toString() }
            .orElse(TAG_VALUE_UNKNOWN_RESOURCE)

        val result: Flux<T> = try {
            func.invoke(request)
                .doOnComplete { logSuccess(request) }
                .doOnError { e -> logFailure(request, e) }
        } catch(e: Exception) {
            logFailure(request, e)
            throw e
        }
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(result, T::class.java)
    }

    fun logSuccess(request: ServerRequest) {
        val resourceId = request.attribute(ATTR_RESOURCE_ID_KEY).map { it.toString() }.orElse(null)
        val startTime = request.attribute(ATTR_START_TIME_KEY).map { it as? Long }.orElse(null)

        requestLoggingSupport.success(request, resourceId, startTime)
    }

    fun logFailure(request: ServerRequest, throwable: Throwable) {
        val resourceId = request.attribute(ATTR_RESOURCE_ID_KEY).map { it.toString() }.orElse(null)
        val startTime = request.attribute(ATTR_START_TIME_KEY).map { it as? Long }.orElse(null)

        requestLoggingSupport.error(request, resourceId, startTime, throwable)
    }
}
