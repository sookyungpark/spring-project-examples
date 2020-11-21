package com.customproject.coffeeshop.consumer.order

import com.customproject.coffeeshop.consumer.BaseConsumer
import com.customproject.coffeeshop.consumer.config.property.OrderProperties
import com.customproject.coffeeshop.consumer.order.OrderRepository
import com.customproject.coffeeshop.consumer.support.JacksonSupport
import com.customproject.coffeeshop.domain.Order
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Component
class OrderConsumer(orderProperties: OrderProperties,
                    private val orderRepository: OrderRepository)
    : BaseConsumer(orderProperties.consumer!!, Schedulers.parallel()) {

    override fun onMessage(msg: String): Mono<Void> {
        return JacksonSupport.deserialize(msg, Order::class.java).let {
            orderRepository.upsert(it)
        }
    }
}