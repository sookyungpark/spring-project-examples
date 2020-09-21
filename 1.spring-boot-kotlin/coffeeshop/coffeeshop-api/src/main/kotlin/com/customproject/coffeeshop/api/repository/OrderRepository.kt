package com.customproject.coffeeshop.api.repository

import com.customproject.coffeeshop.domain.Order
import com.customproject.coffeeshop.domain.OrderMenu
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository


interface OrderRepository {
    fun list(useCache: Boolean = true): List<String>
    fun get(id: String, useCache: Boolean = true): Order?
    fun upsert(order: Order)
}

@Repository
@Qualifier(value = "MockOrderRepository")
class MockOrderRepository : OrderRepository {

    override fun list(useCache: Boolean): List<String> {
        return arrayListOf("order1", "order2")
    }

    override fun get(id: String, useCache: Boolean): Order? {
        return Order(id, menus = listOf(OrderMenu("menuId1", 1)))
    }

    override fun upsert(order: Order) {
        return
    }
}
