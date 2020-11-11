package com.customproject.coffeeshop.domain

data class Menu(val id: String,
                val name: String,
                val cost: Cost,
                val recipe: Recipe)

data class Recipe(val id: String,
                  val contents: List<RecipeContent>)

data class RecipeContent(val id: String,
                         val explanation: String,
                         val ingredients: Map<String, RecipeIngredient>)

data class RecipeIngredient(val quantity: Float,
                            val Ingredient: Ingredient)
