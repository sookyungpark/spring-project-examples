package com.customproject.coffeeshop.api.dao

import com.customproject.coffeeshop.api.config.property.IpBlacklistProperties
import com.github.benmanes.caffeine.cache.Caffeine
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit


@Component
class IpBlacklistDao(private val ipBlacklistProperties: IpBlacklistProperties) {
    companion object {
        private val log = KotlinLogging.logger {}
        private const val BLACKLIST_ALL_CACHE_KEY = "ALL"
    }

    private val ipBlacklistCache = Caffeine
            .newBuilder()
            .maximumSize(ipBlacklistProperties.cache!!.maximumSize!!)
            .expireAfterWrite(ipBlacklistProperties.cache!!.expireAfterWriteMinutes!!, TimeUnit.MINUTES)
            .build<String, Set<String>> {
                fetch()
            }

    fun all() : Set<String> {
        return try {
            ipBlacklistCache.get(BLACKLIST_ALL_CACHE_KEY) ?: emptySet()
        } catch (e: Exception) {
            log.warn(e) { "cannot fetch ip blacklist" }
            return emptySet()
        }
    }

    fun exist(ips: List<String>): Boolean {
        all().let { blacklist ->
            ips.forEach {
                if (blacklist.contains(it)) {
                    return true
                }
            }
        }
        return false
    }

    private fun fetch(): Set<String> {
        TODO()
    }
}

