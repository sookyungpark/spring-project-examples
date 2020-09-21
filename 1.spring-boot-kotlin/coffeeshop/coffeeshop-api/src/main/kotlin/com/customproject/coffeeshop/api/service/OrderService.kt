package com.customproject.coffeeshop.api.service

import com.customproject.coffeeshop.api.exception.NotFoundException
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

@Service
class OrderService(private val orderRepository: OrderRepository) {
    companion object {
        private val log = KotlinLogging.logger {}
    }

    fun list(): List<OrderListResponse> {
        return orderRepository.list()
                .map { OrderListResponse(id = it) }
    }

    fun get(id: String): OrderGetResponse {
        return orderRepository.get(id)
                .let {
                    it?: throw NotFoundException("order : $id not found")
                    OrderGetResponse(id = it.id, menus = it.menus)
                }
    }

    fun create(userId: String, requestBody: CreateOrderRequest) {
        val menus = requestBody.menus
                .map { OrderMenu(it.menuId, it.count) }

        val newOrder = Order(id = OrderSupport.generateId(), menus = menus)
        orderRepository.upsert(newOrder)
    }

    fun update(userId: String, requestBody: UpdateOrderRequest) {
        orderRepository.get(requestBody.id) ?: Order(id = OrderSupport.generateId())
                .let { order ->
                    val menus = requestBody.menus.map { OrderMenu(it.menuId, it.count) }
                    order.apply {
                        this.menus = menus
                    }
                    orderRepository.upsert(order)
                }
    }
}
