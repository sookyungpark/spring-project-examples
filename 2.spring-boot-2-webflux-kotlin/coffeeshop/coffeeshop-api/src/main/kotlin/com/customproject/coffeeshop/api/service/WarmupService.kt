package com.customproject.coffeeshop.api.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class WarmupService {
    companion object {
        private val log = KotlinLogging.logger {}
    }

    fun warmup(): Mono<Void> {
        return Mono.just("").then()
    }
}
