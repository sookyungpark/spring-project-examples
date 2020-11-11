package com.customproject.coffeeshop.domain.request

data class CreateOrderRequest(var menus: List<CreateOrderMenuRequest>)

data class CreateOrderMenuRequest(var menuId: String, var count: Long)

data class UpdateOrderRequest(var id: String, var menus: List<UpdateOrderMenuRequest>)

data class UpdateOrderMenuRequest(var menuId: String, var count: Long)