package com.customproject.coffeeshop.api.support

import com.codahale.metrics.MetricRegistry
import com.customproject.coffeeshop.domain.ServerExchangeCode
import com.customproject.coffeeshop.domain.ServerExchangeLog
import com.customproject.coffeeshop.api.exception.CoffeeshopException
import com.customproject.coffeeshop.api.support.CoffeeshopConstants.Companion.TAG_VALUE_UNKNOWN_RESOURCE
import io.micrometer.prometheus.PrometheusMeterRegistry
import mu.KotlinLogging
import org.apache.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import java.util.concurrent.TimeUnit


@Component
public class RequestLoggingSupport(private val meterRegistry: PrometheusMeterRegistry) {
    companion object {
        private val log = KotlinLogging.logger {}

        private const val DEFAULT_SEPARATOR = "|"

        private const val REQUEST_MONITOR_PREFIX = "coff_reqs"

        private const val TAG_NAME_RESOURCE_ID = "rid"

        private const val METRIC_NAME_TOTAL_COUNT = "ttl"
        private const val METRIC_NAME_SUCCESS_COUNT = "succ"
        private const val METRIC_NAME_FAILURE_COUNT = "fail"
        private const val METRIC_NAME_LATENCY = "ltcy"
    }

    private val requestLogger = KotlinLogging.logger("request-logger")

    fun success(request: ServerRequest, resourceId: String?, startTime: Long?) {
        try {
            logSuccessRequest(request)
            recordSuccessRequestMetric(request, resourceId, startTime)
        } catch (e: Exception) {
            log.warn(e) { "cannot log success" }
        }
    }

    fun error(request: ServerRequest, resourceId: String?, startTime: Long?, throwable: Throwable?) {
        try {
            logFailureRequest(request, throwable)
            recordFailureRequestMetric(request, resourceId, startTime, throwable)
        } catch (e: Exception) {
            log.warn(e) { "cannot log error" }
        }
    }

    private fun logSuccessRequest(request: ServerRequest) {
        ServerExchangeLog(
                code = ServerExchangeCode.SUCCESS,
                uri = request.uri().toASCIIString(),
                method = request.methodName(),
                clientIp = getClientIp(request),
                params = getParams(request),
                headers = getHeaders(request),
                statusCode = HttpStatus.SC_OK,
                exceptionCode = null
        ).let {
            requestLogger.info { it.toString(DEFAULT_SEPARATOR) }
        }
    }

    private fun logFailureRequest(request: ServerRequest, throwable: Throwable?) {
        try {
            var message = ""
            var statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR

            if (throwable is CoffeeshopException) {
                message = "${throwable.webStatus.value()}-${throwable.errorStatus}"
                statusCode = throwable.webStatus.value()
            }

            ServerExchangeLog(
                    code = ServerExchangeCode.FAILURE,
                    uri = request.path(),
                    method = request.methodName(),
                    clientIp = getClientIp(request),
                    params = getParams(request),
                    headers = getHeaders(request),
                    statusCode = statusCode,
                    exceptionCode = message
            ).let {
                requestLogger.info { it.toString(DEFAULT_SEPARATOR) }
            }
        } catch (e: Exception) { }
    }

    private fun recordSuccessRequestMetric(request: ServerRequest, resourceId: String?, startTime: Long?) {
        val tags = arrayOf(TAG_NAME_RESOURCE_ID, resourceId?: TAG_VALUE_UNKNOWN_RESOURCE)

        meterRegistry.counter(MetricRegistry.name(REQUEST_MONITOR_PREFIX, METRIC_NAME_TOTAL_COUNT), *tags)
                .increment()
        meterRegistry.counter(MetricRegistry.name(REQUEST_MONITOR_PREFIX, METRIC_NAME_SUCCESS_COUNT), *tags)
                .increment()

        startTime?.let {
            meterRegistry.timer(MetricRegistry.name(REQUEST_MONITOR_PREFIX, METRIC_NAME_LATENCY), *tags)
                    .record(System.currentTimeMillis() - startTime, TimeUnit.MILLISECONDS)
        }
    }

    private fun recordFailureRequestMetric(request: ServerRequest, resourceId: String?, startTime: Long?, throwable: Throwable?) {
        val tags = arrayOf(TAG_NAME_RESOURCE_ID, resourceId?: TAG_VALUE_UNKNOWN_RESOURCE)

        meterRegistry.counter(MetricRegistry.name(REQUEST_MONITOR_PREFIX, METRIC_NAME_TOTAL_COUNT), *tags)
                .increment()
        meterRegistry.counter(MetricRegistry.name(REQUEST_MONITOR_PREFIX, METRIC_NAME_FAILURE_COUNT), *tags)
                .increment()

        startTime?.let {
            meterRegistry.timer(MetricRegistry.name(REQUEST_MONITOR_PREFIX, METRIC_NAME_LATENCY), *tags)
                    .record(System.currentTimeMillis() - startTime, TimeUnit.MILLISECONDS)
        }
    }

    private fun getClientIp(request: ServerRequest): String? {
        try {
            for (header in CoffeeshopConstants.IP_HEADER_CANDIDATES) {
                val requestHeader = request.headers().header(header)
                if (requestHeader.isNotEmpty()) {
                    return requestHeader.joinToString()
                }
            }
        } catch (e: Exception) {
        }
        return null
    }

    private fun getParams(request: ServerRequest): Map<String, String?>? {
        return emptyMap()
    }

    private fun getHeaders(request: ServerRequest): Map<String, String?>? {
        return emptyMap()
    }
}
