package com.customproject.coffeeshop.api

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootApplication(
        exclude = [DataSourceAutoConfiguration::class,
                   MongoAutoConfiguration::class])
@ComponentScan(
        basePackages = ["com.customproject.coffeeshop"]
)
@EnableWebSecurity
@EnableWebMvc

class ApiApplication

fun main(args: Array<String>) {
    SpringApplication.run(ApiApplication::class.java, *args)
}
