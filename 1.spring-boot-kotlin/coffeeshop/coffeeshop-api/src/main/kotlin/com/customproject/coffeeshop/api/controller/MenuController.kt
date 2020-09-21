package com.customproject.coffeeshop.api.controller

import com.customproject.coffeeshop.api.domain.RequestLogging
import com.customproject.coffeeshop.api.service.MenuService
import com.customproject.coffeeshop.domain.response.MenuGetResponse
import com.customproject.coffeeshop.domain.response.MenuListResponse
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.apache.commons.lang3.Range
import org.springframework.web.bind.annotation.*
import springfox.documentation.swagger2.annotations.EnableSwagger2

@RestController
@RequestMapping("/api/v1/menus")
@EnableSwagger2
class MenuController(private val menuService: MenuService) {

    @RequestLogging
    @GetMapping("/")
    @ApiResponses(ApiResponse(code = 500, message = "unexpected error"))
    fun list(@RequestParam(name = "limit") limit: Long?) : List<MenuListResponse> {
        return menuService.list()
    }

    @RequestLogging
    @GetMapping("/{id}")
    @ApiResponses(ApiResponse(code = 500, message = "unexpected error"))
    public fun get(@PathVariable(name = "id") id: String) : MenuGetResponse {
        return menuService.get(id = id)
    }
}
