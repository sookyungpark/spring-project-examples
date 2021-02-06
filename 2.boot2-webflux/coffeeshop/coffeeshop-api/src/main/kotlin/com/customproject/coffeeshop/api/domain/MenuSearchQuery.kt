package com.customproject.coffeeshop.api.domain

import com.customproject.coffeeshop.domain.Cost
import com.customproject.coffeeshop.domain.MenuCategory

data class MenuSearchQuery(val page: Int,
                           val size: Int,
                           val text: String,
                           val category: MenuCategory?)

data class MenuSearchEsDto(val id: String,
                           val name: String,
                           val cost: Cost,
                           val category: MenuCategory,
                           val recipeId: String) {
    object Fields {
        const val id = "id"
        const val name = "name"
        const val cost = "cost"
        const val category = "category"
        const val recipeId = "recipeId"
    }
}
