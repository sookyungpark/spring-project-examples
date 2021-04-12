package com.customproject.coffeeshop.domain

data class Order(val id: String,
                 val userId: String,
                 val status: OrderStatus = OrderStatus.CREATED,
                 var menus: List<OrderMenu> = emptyList(),
                 val createdAt: Long? = null,
                 val updatedAt: Long? = null,
                 val deliveryStartedAt: Long? = null,
                 val completedAt: Long? = null)

enum class OrderStatus {
    CREATED, READY, IN_PROGRESS, IN_DELIVERY, DONE, FAILED, STOPPED
}

data class OrderMenu(var menuId: String,
                     var count: Long)