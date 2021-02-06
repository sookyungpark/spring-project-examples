package com.customproject.coffeeshop.domain.response

import com.customproject.coffeeshop.domain.Cost

data class MenuListResponse(var id: String)

data class MenuGetResponse(var id: String, var name: String, var cost: Cost)

data class MenuSearchResponse(var id: String, var name: String, var cost: Cost)
