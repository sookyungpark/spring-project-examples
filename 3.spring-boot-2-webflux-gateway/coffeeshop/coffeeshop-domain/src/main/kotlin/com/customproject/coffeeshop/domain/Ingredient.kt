package com.customproject.coffeeshop.domain

data class Ingredient(val id: String,
                      val type: IngredientType)

enum class IngredientType {
    FRUIT, OIL
}
