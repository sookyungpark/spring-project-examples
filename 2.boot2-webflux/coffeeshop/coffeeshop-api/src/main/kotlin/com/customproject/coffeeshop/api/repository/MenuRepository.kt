package com.customproject.coffeeshop.api.repository

import com.customproject.coffeeshop.domain.*
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


interface MenuRepository {
    fun list(useCache: Boolean = true): Flux<String>
    fun get(id: String, useCache: Boolean = true): Mono<Menu>
}

@Repository
@Qualifier(value = "MockMenuRepository")
class MockMenuRepository : MenuRepository {

    override fun list(useCache: Boolean): Flux<String> {
        return Flux.fromArray(arrayOf("menu1", "menu2"))
    }

    override fun get(id: String, useCache: Boolean): Mono<Menu> {
        return Mono.just(
            Menu(
                id = id,
                name = "menuName1",
                cost = Cost(Currency.USD, 1.0f),
                category = MenuCategory.DRINK,
                recipe = Recipe(
                    id = "recipeId1",
                    contents = listOf(
                        RecipeContent("recipeContentId1", "explanation", emptyMap())
                    )
                )))
    }
}
