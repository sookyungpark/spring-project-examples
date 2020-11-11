package com.customproject.coffeeshop.api.config

import com.customproject.coffeeshop.api.config.property.CoffeeshopApiProperties
import com.customproject.coffeeshop.api.filter.*
import com.customproject.coffeeshop.api.handler.*
import com.customproject.coffeeshop.api.handler.internal.InternalOrderHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunctions.resources
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
public class RouterConfiguration(private val coffeeshopApiProperties: CoffeeshopApiProperties,
                                 private val warmupHandler: WarmupHandler,
                                 private val managementHandler: ManagementHandler,
                                 private val orderHandler: OrderHandler,
                                 private val userHandler: UserHandler,
                                 private val menuHandler: MenuHandler,
                                 private val internalOrderHandler: InternalOrderHandler,
                                 private val userAuthCheckFilter: UserAuthCheckFilter,
                                 private val proxyRequestLoggingFilter: ProxyRequestLoggingFilter) {

    @Bean
    @Order(100)
    fun faviconRouter() = resources("/favicon.ico**", ClassPathResource("/static/favicon.ico"))

    @Bean
    @Order(100)
    fun htmlRouter(@Value("classpath:/public/index.html") html: Resource) = router {
        GET("/admin/**") {
            ok().contentType(MediaType.TEXT_HTML).syncBody(html)
        }
    }

    @Bean
    @Order(100)
    fun commonResourcesRouter() = resources("/**", ClassPathResource("/public/"))

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
    .filter(userAuthCheckFilter)

    @Bean
    @Order(10)
    fun internalApiRouter() = router {
        (accept(MediaType.APPLICATION_JSON) and "/api/internal").nest {
            ("/orders").nest {
                GET("/", internalOrderHandler::list)
                GET("/{id}", internalOrderHandler::get)
            }
            ("/users").nest {
                GET("/", userHandler::list)
            }
        }
    }


    @Bean
    @Order(10)
    fun proxyRouteLocator(builder: RouteLocatorBuilder): RouteLocator = builder.routes()
            .route {
                it
                        .path("/api/internal/**")
                        .uri(coffeeshopApiProperties.httpClient?.baseUrl)
                        .order(2)
                        .filter { exchange, chain ->
                            exchange.response.beforeCommit {
                                exchange.response.statusCode = HttpStatus.FORBIDDEN
                                Mono.empty()
                            }
                            return@filter chain.filter(exchange)
                        }
            }
            .route {
                it
                        .path("/api/**")
                        .uri(coffeeshopApiProperties.httpClient?.baseUrl)
                        .order(3)
                        .filter(proxyRequestLoggingFilter)
            }
            .build()
}
