package com.customproject.coffeeshop.api.controller

import com.customproject.coffeeshop.api.domain.Authorized
import com.customproject.coffeeshop.api.domain.AuthorizedMethod
import com.customproject.coffeeshop.api.domain.RequestLogging
import com.customproject.coffeeshop.api.domain.UserProfile
import com.customproject.coffeeshop.api.service.OrderService
import com.customproject.coffeeshop.api.service.UserService
import com.customproject.coffeeshop.domain.response.OrderGetResponse
import com.customproject.coffeeshop.domain.response.OrderListResponse
import com.customproject.coffeeshop.domain.response.UserListResponse
import com.customproject.coffeeshop.domain.response.UserProfileGetResponse
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.apache.commons.lang3.Range
import org.springframework.web.bind.annotation.*
import springfox.documentation.swagger2.annotations.EnableSwagger2


@RestController
@RequestMapping("/api/v1/users")
@EnableSwagger2
class UserController(private val userService: UserService) {

    @RequestLogging
    @GetMapping("/")
    @ApiResponses(ApiResponse(code = 500, message = "unexpected error"))
    fun list(@RequestParam(name = "limit") limit: Long?) : List<UserListResponse> {
        return userService.list()
    }

    @RequestLogging
    @GetMapping("/profile")
    @ApiResponses(ApiResponse(code = 500, message = "unexpected error"))
    public fun get(@Authorized(forced = true) @AuthorizedMethod("TOKEN") userProfile: UserProfile) : UserProfileGetResponse {
        return userService.getProfile(id = userProfile.userKey)
    }
}
