package com.customproject.coffeeshop.api.support

import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.prometheus.client.CollectorRegistry

import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.config.NamingConvention
import java.util.Objects.requireNonNull


object PrometheusMeterRegistries {

    @JvmOverloads
    fun newRegistry(registry: CollectorRegistry = CollectorRegistry(), clock: Clock = Clock.SYSTEM): PrometheusMeterRegistry {
        val meterRegistry = PrometheusMeterRegistry(
                PrometheusConfig.DEFAULT,
                requireNonNull(registry, "registry"),
                requireNonNull(clock, "clock"))
        meterRegistry.config().namingConvention(NamingConvention.camelCase)
        return meterRegistry
    }
}
