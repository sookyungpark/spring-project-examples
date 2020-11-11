package com.customproject.coffeeshop.api.config.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "internal-api")
data class InternalApiProperties(var headerSecretKey: String = "")