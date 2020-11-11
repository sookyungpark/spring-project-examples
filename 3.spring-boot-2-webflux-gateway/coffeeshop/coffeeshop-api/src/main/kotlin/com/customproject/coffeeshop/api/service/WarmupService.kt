package com.customproject.coffeeshop.api.service

import com.customproject.coffeeshop.domain.ResponseCode
import mu.KotlinLogging
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class WarmupService {
    companion object {
        private val log = KotlinLogging.logger {}
    }

    fun warmup(): Mono<ResponseCode> {
        return Mono.just(ResponseCode.SUCC)
    }
}
