package com.customproject.coffeeshop.api.service

import com.customproject.coffeeshop.api.ApiTestSupport
import com.customproject.coffeeshop.api.repository.MenuRepository
import com.customproject.coffeeshop.api.repository.MenuSearchRepository
import com.customproject.coffeeshop.domain.response.MenuListResponse
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import reactor.core.publisher.Flux

@RunWith(MockitoJUnitRunner::class)
class MenuServiceTest {

    private val menuRepository: MenuRepository = Mockito.mock(MenuRepository::class.java)
    private val menuSearchRepository: MenuSearchRepository = Mockito.mock(MenuSearchRepository::class.java)
    private val menuService: MenuService = MenuService(menuRepository, menuSearchRepository)

    @Test
    fun list() {

        val countLimit = 10L

        val menuIds = listOf("menuId")
        val expectedResult = menuIds.map { MenuListResponse(id = it) }

        // mock
        Mockito.`when`(menuRepository.list()).thenReturn(Flux.fromStream { menuIds.stream() })
        // when
        val actualResult = menuService.list(countLimit).collectList().block()

        // then
        Assert.assertNotNull(actualResult)
        Assert.assertEquals(ApiTestSupport.objectMapper.writeValueAsString(expectedResult), ApiTestSupport.objectMapper.writeValueAsString(actualResult))
    }
}
