package com.customproject.coffeeshop.api.config.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "menu")
data class MenuProperties(var search: MenuSearchProperties? = MenuSearchProperties())
data class MenuSearchProperties(var vectorScoring: Boolean? = false)