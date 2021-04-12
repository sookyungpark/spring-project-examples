package com.customproject.coffeeshop.api.service

import com.customproject.coffeeshop.api.repository.OrderRepository
import com.customproject.coffeeshop.api.support.OrderSupport
import com.customproject.coffeeshop.domain.Order
import com.customproject.coffeeshop.domain.OrderMenu
import com.customproject.coffeeshop.domain.request.CreateOrderRequest
import com.customproject.coffeeshop.domain.request.UpdateOrderRequest
import com.customproject.coffeeshop.domain.response.OrderGetResponse
import com.customproject.coffeeshop.domain.response.OrderListResponse
import mu.KotlinLogging
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OrderService(private val orderRepository: OrderRepository) {
    companion object {
        private val log = KotlinLogging.logger {}
    }

    fun list(countLimit: Long): Flux<OrderListResponse> {
        return orderRepository.list()
                .map {
                    OrderListResponse(id = it)
                }
                .limitRequest(countLimit)
    }

    fun get(id: String): Mono<OrderGetResponse> {
        return orderRepository.get(id)
                .map {
                    OrderGetResponse(id = it.id, menus = it.menus)
                }
    }

    fun create(userId: String, requestBody: Mono<CreateOrderRequest>): Mono<Void> {
        return requestBody
                .flatMap {
                    val menus = it.menus
                        .map { OrderMenu(it.menuId, it.count) }

                    val newOrder = Order(
                        id = OrderSupport.generateId(),
                        userId = OrderSupport.generateId(),
                        menus = menus)
                    orderRepository.upsert(newOrder)
                }
    }

    fun update(userId: String, requestBody: Mono<UpdateOrderRequest>): Mono<Void> {
        return requestBody
                .flatMap {
                    orderRepository.get(it.id).defaultIfEmpty(Order(
                            id = OrderSupport.generateId(),
                            userId = OrderSupport.generateId()))
                        .map { order ->
                            val menus = it.menus
                                    .map { OrderMenu(it.menuId, it.count) }

                            order.apply {
                                this.menus = menus
                            }
                        }
                }
                .flatMap {
                    orderRepository.upsert(it)
                }
    }
}
