package com.customproject.coffeeshop.api.controller.internal

import com.customproject.coffeeshop.api.domain.Internal
import com.customproject.coffeeshop.api.domain.RequestLogging
import com.customproject.coffeeshop.api.service.OrderService
import com.customproject.coffeeshop.domain.response.OrderGetResponse
import com.customproject.coffeeshop.domain.response.OrderListResponse
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.apache.commons.lang3.Range
import org.springframework.web.bind.annotation.*
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Internal
@RestController
@RequestMapping("/api/v1/internal/orders")
@EnableSwagger2
class InternalOrderController(private val orderService: OrderService) {

    @RequestLogging
    @GetMapping("/")
    @ApiResponses(ApiResponse(code = 500, message = "unexpected error"))
    fun list(@RequestParam(name = "limit") limit: Long?) : List<OrderListResponse> {
        return orderService.list()
    }

    @RequestLogging
    @GetMapping("/{id}")
    @ApiResponses(ApiResponse(code = 500, message = "unexpected error"))
    public fun get(@PathVariable(name = "id") id: String) : OrderGetResponse {
        return orderService.get(id = id)
    }
}
