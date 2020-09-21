package com.customproject.coffeeshop.api.repository

import com.customproject.coffeeshop.domain.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository


interface MenuRepository {
    fun list(useCache: Boolean = true): List<String>
    fun get(id: String, useCache: Boolean = true): Menu
}

@Repository
@Qualifier(value = "MockMenuRepository")
class MockMenuRepository : MenuRepository {

    override fun list(useCache: Boolean): List<String> {
        return arrayListOf("menu1", "menu2")
    }

    override fun get(id: String, useCache: Boolean): Menu {
        return Menu(
                id = id,
                name = "menuName1",
                cost = Cost(Currency.USD, 1.0f),
                recipe = Recipe(
                    id = "recipeId1",
                    contents = listOf(
                        RecipeContent("recipeContentId1", "explanation", emptyMap())
                    )
                ))
    }
}
