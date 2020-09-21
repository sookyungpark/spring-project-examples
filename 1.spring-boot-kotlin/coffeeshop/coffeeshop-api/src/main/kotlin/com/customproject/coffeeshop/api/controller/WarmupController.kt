package com.customproject.coffeeshop.api.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/warmup")
class WarmupController {

    @GetMapping(value = ["/"])
    fun warmup(): String? {
        return ""
    }
}
