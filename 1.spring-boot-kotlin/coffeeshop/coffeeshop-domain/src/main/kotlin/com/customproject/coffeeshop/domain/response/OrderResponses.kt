package com.customproject.coffeeshop.domain.response

import com.customproject.coffeeshop.domain.OrderMenu

data class OrderListResponse(var id: String)

data class OrderGetResponse(var id: String, var menus: List<OrderMenu>)