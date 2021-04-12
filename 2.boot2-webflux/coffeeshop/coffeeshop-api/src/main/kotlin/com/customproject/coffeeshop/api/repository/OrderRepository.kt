package com.customproject.coffeeshop.api.repository

import com.customproject.coffeeshop.domain.Order
import com.customproject.coffeeshop.domain.OrderMenu
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


interface OrderRepository {
    fun all(useCache: Boolean = true): Flux<Order>
    fun list(useCache: Boolean = true): Flux<String>
    fun get(id: String, useCache: Boolean = true): Mono<Order>
    fun upsert(order: Order): Mono<Void>
}

@Repository
@Qualifier(value = "MockOrderRepository")
class MockOrderRepository : OrderRepository {
    override fun all(useCache: Boolean): Flux<Order> {
        TODO("Not yet implemented")
    }

    override fun list(useCache: Boolean): Flux<String> {
        return Flux.fromArray(arrayOf("order1", "order2"))
    }

    override fun get(id: String, useCache: Boolean): Mono<Order> {
        return Mono.just(Order(id, "user_id", menus = listOf(OrderMenu("menuId1", 1))))
    }

    override fun upsert(order: Order): Mono<Void> {
        return Mono.just("").then()
    }
}
