package com.customproject.coffeeshop.api.repository

import com.customproject.coffeeshop.api.config.property.MenuProperties
import com.customproject.coffeeshop.api.domain.MenuSearchEsDto
import com.customproject.coffeeshop.api.domain.MenuSearchQuery
import com.customproject.coffeeshop.api.exception.UnexpectedMenuException
import com.customproject.coffeeshop.domain.*
import com.google.common.collect.ImmutableMap
import io.micrometer.core.instrument.util.IOUtils
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.index.query.functionscore.ScriptScoreQueryBuilder
import org.elasticsearch.script.Script
import org.elasticsearch.script.ScriptType
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.ClassPathResource
import org.springframework.data.domain.PageRequest
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux


interface MenuSearchRepository {
    fun search(menuSearchQuery: MenuSearchQuery): Flux<Menu>
}

@Repository
@Qualifier(value = "MockMenuSearchRepository")
class MockMenuSearchRepository : MenuSearchRepository {
    override fun search(menuSearchQuery: MenuSearchQuery): Flux<Menu> {
        val menu1 = Menu(
                id = "menuid1",
                name = "menuName1",
                cost = Cost(Currency.USD, 1.0f),
                category = MenuCategory.DRINK,
                recipe = Recipe(
                        id = "recipeId1",
                        contents = listOf(
                                RecipeContent("recipeContentId1", "explanation", emptyMap())
                        )
                ))
        return Flux.fromArray(arrayOf(menu1))
    }
}

// TODO : add curator option on resource
@Repository
@Qualifier(value = "EsMenuSearchRepository")
class EsMenuSearchRepository(val menuProperties: MenuProperties,
                             val reactiveElasticsearchOperations: ReactiveElasticsearchOperations,
                             val menuRepository: MenuRepository) : MenuSearchRepository {
    companion object {
        private const val INDEX_NAME = "menu"
        private val searchScoringScript: String = try {
            ClassPathResource("es/menu-search-query.painless").inputStream.use {
                IOUtils.toString(it)
            }
        } catch (e: Exception) {
            throw UnexpectedMenuException("cannot load query")
        }
    }

    override fun search(menuSearchQuery: MenuSearchQuery): Flux<Menu> {
        val esQuery = getEsQuery(menuSearchQuery)
        return reactiveElasticsearchOperations.search(esQuery, MenuSearchEsDto::class.java, IndexCoordinates.of(INDEX_NAME))
                .flatMap { menuRepository.get(it.content.id) }
    }


    private fun getEsQuery(menuSearchQuery: MenuSearchQuery): NativeSearchQuery {
        var queryBuilder: QueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery(MenuSearchEsDto.Fields.name, menuSearchQuery.text))

        // if vector scoring is necessary, wrap query with vector script score
        if (menuProperties.search!!.vectorScoring!!) {
            getQueryVector(menuSearchQuery)?.let {
                val scriptParams: Map<String, Any> = ImmutableMap.of("query_vec", it)
                val script = Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG, searchScoringScript, scriptParams)
                queryBuilder = ScriptScoreQueryBuilder(queryBuilder, script)
            }
        }

        val filterBuilder = QueryBuilders.boolQuery().apply {
            menuSearchQuery.category?.let {
                must(QueryBuilders.termQuery(MenuSearchEsDto.Fields.category, menuSearchQuery.category))
            }
        }

        return NativeSearchQueryBuilder()
                .withPageable(PageRequest.of(menuSearchQuery.page, menuSearchQuery.size))
                .withQuery(queryBuilder)
                .withFilter(filterBuilder)
                .build()
    }

    private fun getQueryVector(query: MenuSearchQuery): List<Float>? {
        // dummy
        return listOf()
    }
}