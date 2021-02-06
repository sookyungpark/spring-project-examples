package com.customproject.coffeeshop.api.service

import com.customproject.coffeeshop.api.domain.MenuSearchQuery
import com.customproject.coffeeshop.api.repository.MenuRepository
import com.customproject.coffeeshop.api.repository.MenuSearchRepository
import com.customproject.coffeeshop.domain.Menu
import com.customproject.coffeeshop.domain.response.MenuGetResponse
import com.customproject.coffeeshop.domain.response.MenuListResponse
import com.customproject.coffeeshop.domain.response.MenuSearchResponse
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Service
class MenuService(private val menuRepository: MenuRepository,
                  @Qualifier("MockMenuSearchRepository") private val menuSearchRepository: MenuSearchRepository) {
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

    fun search(menuSearchQuery: MenuSearchQuery): Flux<MenuSearchResponse> {
        return menuSearchRepository.search(menuSearchQuery)
                .map {
                    MenuSearchResponse(id = it.id, name = it.name, cost = it.cost)
                }
    }
}
