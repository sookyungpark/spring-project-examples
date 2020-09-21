package com.customproject.coffeeshop.api.service

import com.customproject.coffeeshop.api.ApiTestSupport
import com.customproject.coffeeshop.api.repository.MenuRepository
import com.customproject.coffeeshop.domain.response.MenuListResponse
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MenuServiceTest {

    private val menuRepository: MenuRepository = Mockito.mock(MenuRepository::class.java)
    private val menuService: MenuService = MenuService(menuRepository)

    @Test
    fun list() {

        val countLimit = 10L

        val menuIds = listOf("menuId")
        val expectedResult = menuIds.map { MenuListResponse(id = it) }

        // mock
        Mockito.`when`(menuRepository.list()).thenReturn(menuIds)
        // when
        val actualResult = menuService.list()

        // then
        Assert.assertNotNull(actualResult)
        Assert.assertEquals(ApiTestSupport.objectMapper.writeValueAsString(expectedResult), ApiTestSupport.objectMapper.writeValueAsString(actualResult))
    }
}
