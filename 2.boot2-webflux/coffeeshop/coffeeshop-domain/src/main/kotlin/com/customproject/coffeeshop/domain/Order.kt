package com.customproject.coffeeshop.domain

data class Order(val id: String,
                 var menus: List<OrderMenu> = emptyList())

data class OrderMenu(var menuId: String,
                     var count: Long)