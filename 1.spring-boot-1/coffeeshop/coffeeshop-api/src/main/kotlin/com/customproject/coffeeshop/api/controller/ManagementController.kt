package com.customproject.coffeeshop.api.controller

import io.micrometer.prometheus.PrometheusMeterRegistry
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/management")
class ManagementController(private val prometheusMeterRegistry: PrometheusMeterRegistry) {

    @GetMapping(value = ["/prometheus"])
    fun getPrometheusMetric(): String? {
        return prometheusMeterRegistry.scrape()
    }
}