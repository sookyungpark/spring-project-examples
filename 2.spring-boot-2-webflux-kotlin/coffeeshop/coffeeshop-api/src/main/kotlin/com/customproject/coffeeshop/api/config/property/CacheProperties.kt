package com.customproject.coffeeshop.api.config.property

data class CacheProperties(var maximumSize: Long? = 1000L,
                           var expireAfterAccessMinutes: Long? = 10,
                           var expireAfterWriteMinutes: Long? = 10)