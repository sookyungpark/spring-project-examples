package com.customproject.coffeeshop.consumer.config.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "application")
data class ApplicationProperties(var phase: String = "LOCAL")