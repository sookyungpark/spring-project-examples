package com.customproject.coffeeshop.api.config.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

data class ClientProperties(var socketTimeoutMillis: Int? = 5000,
                            var connectTimeoutMillis: Int? = 5000,
                            var pooledIdleConnectionTtlMillis: Long? = 300000L,
                            var pooledMaxTotal: Int? = 500,
                            var useKeepalive: Boolean? = true,
                            var retryLimit: Int? = 1)

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "httpclient")
data class HttpClientProperties(val default: ClientProperties? = ClientProperties())