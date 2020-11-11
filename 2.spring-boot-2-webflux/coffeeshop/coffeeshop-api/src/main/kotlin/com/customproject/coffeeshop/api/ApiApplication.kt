package com.customproject.coffeeshop.api

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientAutoConfiguration
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration

@SpringBootApplication(
        exclude = [DataSourceAutoConfiguration::class,
                   RestClientAutoConfiguration::class,
                   ErrorMvcAutoConfiguration::class,
                   GsonAutoConfiguration::class,
                   WebMvcAutoConfiguration::class]
)
//@ComponentScan(basePackages = [
                   //"com.coffeeshop.common.support"
//                   ],
//               excludeFilters = [ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = [ExcludeTargetClass::class])]
//)
class ApiApplication

fun main(args: Array<String>) {
    SpringApplication.run(ApiApplication::class.java, *args)
}
