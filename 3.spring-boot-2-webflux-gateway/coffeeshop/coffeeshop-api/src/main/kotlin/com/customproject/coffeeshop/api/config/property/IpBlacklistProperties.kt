package com.customproject.coffeeshop.api.config.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "ip-blacklist")
data class IpBlacklistProperties(var cache: IpBlacklistCacheProperties? = IpBlacklistCacheProperties())

data class IpBlacklistCacheProperties(var maximumSize: Long? = 1000,
                                      var expireAfterWriteMinutes: Long? = 10)