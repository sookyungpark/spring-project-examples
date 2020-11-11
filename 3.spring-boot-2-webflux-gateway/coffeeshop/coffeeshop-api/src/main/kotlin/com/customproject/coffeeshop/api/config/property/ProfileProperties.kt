package com.customproject.coffeeshop.api.config.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "profile")
data class ProfileProperties(var cache: CacheProperties? = CacheProperties(),
                             var httpClient: HttpClientProperties? = ProfileHttpClientProperties())

data class ProfileHttpClientProperties(override var baseUrl: String? = "http://coffeeshop-profile-service.domain.com",
                                       override var connectTimeoutMillis: Int? = 5000,
                                       override var readTimeoutSec: Int? = 5,
                                       override var writeTimeoutSec: Int? = 5,
                                       override var soKeepAlive: Boolean? = true,
                                       override var tcpNoDelay: Boolean? = true,
                                       override var reuseAddr: Boolean? = true): HttpClientProperties()