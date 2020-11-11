package com.customproject.coffeeshop.api.service

import com.customproject.coffeeshop.api.repository.MenuRepository
import com.customproject.coffeeshop.domain.response.MenuGetResponse
import com.customproject.coffeeshop.domain.response.MenuListResponse
import mu.KotlinLogging
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Service
class MenuService(private val menuRepository: MenuRepository) {
    companion object {
        private val log = KotlinLogging.logger {}
    }

    fun list(countLimit: Long): Flux<MenuListResponse> {
        return menuRepository.list()
                .map {
                    MenuListResponse(id = it)
                }
                .limitRequest(countLimit)
    }

    fun get(id: String): Mono<MenuGetResponse> {
        return menuRepository.get(id)
                .map {
                    MenuGetResponse(id = it.id, name = it.name, cost = it.cost)
                }
    }
}
