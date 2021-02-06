package com.customproject.coffeeshop.api.config.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "spring.data.elasticsearch.client.reactive")
data class EsProperties(var endpoints: String? = "localhost:9200",
                        var connectionTimeout: Long? = 10,
                        var socketTimeout: Long? = 3)