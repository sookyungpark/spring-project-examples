package com.customproject.coffeeshop.api.service

import com.customproject.coffeeshop.api.repository.MenuRepository
import com.customproject.coffeeshop.domain.response.MenuGetResponse
import com.customproject.coffeeshop.domain.response.MenuListResponse
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class MenuService(private val menuRepository: MenuRepository) {
    companion object {
        private val log = KotlinLogging.logger {}
    }

    fun list(): List<MenuListResponse> {
        return menuRepository.list()
                .map { MenuListResponse(id = it) }
    }

    fun get(id: String): MenuGetResponse {
        return menuRepository.get(id)
                .let { MenuGetResponse(id = it.id, name = it.name, cost = it.cost) }
    }
}
