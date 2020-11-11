package com.customproject.coffeeshop.api.config.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "coffeeshop-api")
data class CoffeeshopApiProperties(var httpClient: HttpClientProperties? = CoffeeshopHttpClientProperties())

data class CoffeeshopHttpClientProperties(override var baseUrl: String? = "http://coffeeshop-api.domain.com",
                                          override var connectTimeoutMillis: Int? = 5000,
                                          override var readTimeoutSec: Int? = 5,
                                          override var writeTimeoutSec: Int? = 5,
                                          override var soKeepAlive: Boolean? = true,
                                          override var tcpNoDelay: Boolean? = true,
                                          override var reuseAddr: Boolean? = true) : HttpClientProperties()