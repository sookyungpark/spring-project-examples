package com.customproject.coffeeshop.api.config

import com.customproject.coffeeshop.api.handler.*
import com.customproject.coffeeshop.api.handler.internal.InternalOrderHandler
import com.customproject.coffeeshop.api.handler.internal.InternalUserHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunctions.resources
import org.springframework.web.reactive.function.server.router

@Configuration
public class RouterConfig(private val warmupHandler: WarmupHandler,
                          private val managementHandler: ManagementHandler,
                          private val orderHandler: OrderHandler,
                          private val userHandler: UserHandler,
                          private val menuHandler: MenuHandler,
                          private val internalUserHandler: InternalUserHandler,
                          private val internalOrderHandler: InternalOrderHandler) {

    @Bean
    @Order(100)
    fun faviconRouter() = resources("/favicon.ico**", ClassPathResource("/static/favicon.ico"))

    @Bean
    @Order(100)
    fun resourcesRouter() = resources("/docs/**", ClassPathResource("/public/docs/"))

    @Bean
    @Order(100)
    fun commonRouter() = router {
        (accept(MediaType.APPLICATION_JSON) and "/warmup").nest {
            GET("/", warmupHandler::warmup)
        }
        ("/management").nest {
            GET("/prometheus", managementHandler::scrape)
        }
    }

    @Bean
    @Order(1)
    fun apiRouter() = router {
        (accept(MediaType.APPLICATION_JSON) and "/api").nest {
            ("/menus").nest {
                GET("/", menuHandler::list)
                GET("/{id}", menuHandler::get)
                POST("/search", menuHandler::search)
            }
        }
    }

    @Bean
    @Order(5)
    fun apiWithAuthRouter() = router {
        (accept(MediaType.APPLICATION_JSON) and "/api").nest {
            ("/orders").nest {
                POST("/", orderHandler::create)
                PUT("/{id}", orderHandler::update)
            }
            ("/users").nest {
                GET("/profile", userHandler::getProfile)
            }
        }
    }
    //.filter(userAuthCheckFilter)

    @Bean
    @Order(10)
    fun internalApiRouter() = router {
        (accept(MediaType.APPLICATION_JSON) and "/api/internal").nest {
            ("/orders").nest {
                GET("/", internalOrderHandler::list)
                GET("/{id}", internalOrderHandler::get)
            }
            ("/users").nest {
                GET("/", internalUserHandler::list)
            }

        }
    }
    //.filter(internalAuthCheckFilter)
}
