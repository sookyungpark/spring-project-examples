package com.customproject.coffeeshop.api.config.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "auth")
data class AuthProperties(var httpClient: HttpClientProperties? = AuthHttpClientProperties())

data class AuthHttpClientProperties(override var baseUrl: String? = "http://coffeeshop-auth-service.domain.com",
                                    override var connectTimeoutMillis: Int? = 5000,
                                    override var readTimeoutSec: Int? = 5,
                                    override var writeTimeoutSec: Int? = 5,
                                    override var soKeepAlive: Boolean? = true,
                                    override var tcpNoDelay: Boolean? = true,
                                    override var reuseAddr: Boolean? = true): HttpClientProperties()