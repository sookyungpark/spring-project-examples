package com.customproject.coffeeshop.api.filter

import com.codahale.metrics.MetricRegistry
import com.customproject.coffeeshop.domain.ServerExchangeCode
import com.customproject.coffeeshop.domain.ServerExchangeLog
import com.customproject.coffeeshop.api.exception.CoffeeshopException
import com.customproject.coffeeshop.api.support.FilterOrder
import com.customproject.coffeeshop.api.support.CoffeeshopConstants
import com.customproject.coffeeshop.api.support.CoffeeshopConstants.Companion.TAG_VALUE_UNKNOWN_RESOURCE
import io.micrometer.core.instrument.MeterRegistry
import mu.KotlinLogging
import org.apache.http.HttpStatus
import org.springframework.core.Ordered
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import reactor.core.publisher.Mono
import reactor.core.publisher.Signal
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates


public class ExternalRequestLoggingFilter(private val serviceTag: String, private val meterRegistry: MeterRegistry)
    : ExchangeFilterFunction, Ordered {
    companion object {
        private val log = KotlinLogging.logger {}

        private const val DEFAULT_SEPARATOR = "::"

        private const val METRIC_PREFIX = "coff_ext_reqs"

        private const val TAG_NAME_RESOURCE_ID = "rid"
        private const val TAG_NAME_SERVICE_TAG = "stag"

        private const val METRIC_NAME_TOTAL_COUNT = "ttl"
        private const val METRIC_NAME_SUCCESS_COUNT = "succ"
        private const val METRIC_NAME_FAILURE_COUNT = "fail"
        private const val METRIC_NAME_LATENCY = "ltcy"

        private val requestLogger = KotlinLogging.logger("ext-request-logger")
    }

    var startTimeKey: String by Delegates.notNull()

    init {
        startTimeKey = "_STTK__$serviceTag"
    }

    override fun getOrder(): Int {
        return FilterOrder.EXTERNAL_REQUEST_LOGGING_FILTER.value
    }

    public override fun filter(request: ClientRequest, exchangeFunction: ExchangeFunction): Mono<ClientResponse> {
        return exchangeFunction.exchange(request)
                .doOnEach {
                    if (!it.isOnComplete) {
                        return@doOnEach
                    }
                    if (it?.isOnError == true) {
                        error(request, it)
                        return@doOnEach
                    }
                    success(request, it)
                }
                .subscriberContext { context -> context.put(startTimeKey, System.currentTimeMillis()) }
    }

    private fun success(request: ClientRequest?, signal: Signal<ClientResponse?>?) {
        request ?: return
        try {
            logSuccessRequest(request)
            recordSuccessRequestMetric(request, signal)
        } catch (e: Exception) {
            log.warn(e) { "cannot log external success" }
        }
    }

    fun error(request: ClientRequest?, signal: Signal<ClientResponse?>?) {
        request ?: return
        try {
            logFailureRequest(request, signal)
            recordFailureRequestMetric(request, signal)
        } catch (e: Exception) {
            log.warn(e) { "cannot log external error" }
        }
    }

    private fun logSuccessRequest(request: ClientRequest) {
        ServerExchangeLog(
                code = ServerExchangeCode.SUCCESS,
                uri = request.url().path,
                method = request.method().name,
                clientIp = getClientIp(request),
                params = getParams(request),
                headers = getHeaders(request),
                statusCode = HttpStatus.SC_OK,
                exceptionCode = null
        ).let {
            requestLogger.info { it.toString(DEFAULT_SEPARATOR) }
        }
    }

    private fun logFailureRequest(request: ClientRequest, signal: Signal<ClientResponse?>?) {
        try {
            var exceptionCode = ""
            var statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR

            val throwable = signal?.throwable
            if (throwable is CoffeeshopException) {
                exceptionCode = "${throwable.webStatus.value()}_${throwable.errorStatus}"
                statusCode = throwable.webStatus.value()
            }

            ServerExchangeLog(
                    code = ServerExchangeCode.FAILURE,
                    uri = request.url().path,
                    method = request.method().name,
                    clientIp = getClientIp(request),
                    params = getParams(request),
                    headers = getHeaders(request),
                    statusCode = statusCode,
                    exceptionCode = exceptionCode
            ).let {
                requestLogger.info { it.toString(DEFAULT_SEPARATOR) }
            }
        } catch (e: Exception) { }
    }

    private fun recordSuccessRequestMetric(request: ClientRequest, signal: Signal<ClientResponse?>?) {
        val tags = arrayOf(TAG_NAME_SERVICE_TAG, serviceTag, TAG_NAME_RESOURCE_ID, getResourceId(request))

        meterRegistry.counter(MetricRegistry.name(METRIC_PREFIX, METRIC_NAME_TOTAL_COUNT), *tags)
                .increment()
        meterRegistry.counter(MetricRegistry.name(METRIC_PREFIX, METRIC_NAME_SUCCESS_COUNT), *tags)
                .increment()

        signal?.context?: return
        signal.context.getOrEmpty<Long>(startTimeKey).ifPresent { startTime ->
            meterRegistry.timer(MetricRegistry.name(METRIC_PREFIX, METRIC_NAME_LATENCY), *tags)
                    .record(System.currentTimeMillis() - startTime, TimeUnit.MILLISECONDS)
        }
    }

    private fun recordFailureRequestMetric(request: ClientRequest, signal: Signal<ClientResponse?>?) {
        val tags = arrayOf(TAG_NAME_SERVICE_TAG, serviceTag, TAG_NAME_RESOURCE_ID, getResourceId(request))

        meterRegistry.counter(MetricRegistry.name(METRIC_PREFIX, METRIC_NAME_TOTAL_COUNT), *tags)
                .increment()
        meterRegistry.counter(MetricRegistry.name(METRIC_PREFIX, METRIC_NAME_FAILURE_COUNT), *tags)
                .increment()

        signal?.context?: return
        signal.context.getOrEmpty<Long>(startTimeKey).ifPresent { startTime ->
            meterRegistry.timer(MetricRegistry.name(METRIC_PREFIX, METRIC_NAME_LATENCY), *tags)
                    .record(System.currentTimeMillis() - startTime, TimeUnit.MILLISECONDS)
        }
    }

    private fun getClientIp(request: ClientRequest): String? {
        try {
            for (header in CoffeeshopConstants.IP_HEADER_CANDIDATES) {
                val requestHeader = request.headers().getFirst(header)
                if (requestHeader != null) {
                    return requestHeader
                }
            }
        } catch (e: Exception) {
        }
        return null
    }

    private fun getParams(request: ClientRequest): Map<String, String?>? {
        return emptyMap()
    }

    private fun getHeaders(request: ClientRequest): Map<String, String?>? {
        return emptyMap()
    }

    private fun getResourceId(request: ClientRequest): String {
        return request.attribute(CoffeeshopConstants.ATTR_RESOURCE_ID_KEY)
            .map { it.toString() }
            .orElse(TAG_VALUE_UNKNOWN_RESOURCE)
    }
}
