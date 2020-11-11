package com.customproject.coffeeshop.api.filter

import com.codahale.metrics.MetricRegistry
import com.customproject.coffeeshop.domain.ServerExchangeCode
import com.customproject.coffeeshop.domain.ServerExchangeLog
import com.customproject.coffeeshop.api.exception.CoffeeshopException
import com.customproject.coffeeshop.api.support.FilterOrder
import com.customproject.coffeeshop.api.support.CoffeeshopConstants
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

        private const val DEFAULT_SEPARATOR = "|"

        private const val METRIC_PREFIX = "coffeeshop_ext_requests"
        private const val TAG_NAME_SERVICE_TAG = "stag"

        private const val METRIC_NAME_TOTAL_COUNT = "total"
        private const val METRIC_NAME_SUCCESS_COUNT = "success"
        private const val METRIC_NAME_FAILURE_COUNT = "failure"
        private const val METRIC_NAME_LATENCY = "latency"

        private val requestLogger = KotlinLogging.logger("ext-request-logger")
    }

    var startTimeKey: String by Delegates.notNull()

    init {
        startTimeKey = "_STT__$serviceTag"
    }

    override fun getOrder(): Int {
        return FilterOrder.REQUEST_LOGGING_FILTER.value
    }

    override fun filter(request: ClientRequest, exchangeFunction: ExchangeFunction): Mono<ClientResponse> {
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
                .subscriberContext {context -> context.put(startTimeKey, System.currentTimeMillis()) }
    }

    fun success(request: ClientRequest?, signal: Signal<ClientResponse?>?) {
        request ?: return
        try {
            logSuccessRequest(request)
            recordSuccessRequestMetric(request, signal)
        } catch (e: Exception) {
            log.warn(e) { "cannot log external success" }
            // do nothing
        }
    }

    fun error(request: ClientRequest?, signal: Signal<ClientResponse?>?) {
        request ?: return
        try {
            logFailureRequest(request, signal)
            recordFailureRequestMetric(request, signal)
        } catch (e: Exception) {
            log.warn(e) { "cannot log external error" }
            // do nothing
        }
    }

    private fun logSuccessRequest(request: ClientRequest) {
        ServerExchangeLog(
                code = ServerExchangeCode.SUCCESS,
                uri = request.url().path,
                method = request.method().name,
                clientIp = getClientIp(request),
                statusCode = HttpStatus.SC_OK
        ).let {
            requestLogger.info { it.toString(DEFAULT_SEPARATOR) }
        }
    }

    private fun logFailureRequest(request: ClientRequest, signal: Signal<ClientResponse?>?) {
        try {
            var statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR

            val throwable = signal?.throwable
            if (throwable is CoffeeshopException) {
                statusCode = throwable.webStatus.value()
            }

            ServerExchangeLog(
                    code = ServerExchangeCode.FAILURE,
                    uri = request.url().path,
                    method = request.method().name,
                    clientIp = getClientIp(request),
                    statusCode = statusCode
            ).let {
                requestLogger.info { it.toString(DEFAULT_SEPARATOR) }
            }
        } catch (e: Exception) {
            // do nothing
        }
    }

    private fun recordSuccessRequestMetric(request: ClientRequest, signal: Signal<ClientResponse?>?) {
        val tags = arrayOf(TAG_NAME_SERVICE_TAG, serviceTag)

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
        val tags = arrayOf(TAG_NAME_SERVICE_TAG, serviceTag)

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
}
