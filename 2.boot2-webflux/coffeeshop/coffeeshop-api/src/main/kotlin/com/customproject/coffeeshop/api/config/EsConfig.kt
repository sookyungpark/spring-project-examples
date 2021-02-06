package com.customproject.coffeeshop.api.config

import com.customproject.coffeeshop.api.config.property.EsProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient
import org.springframework.data.elasticsearch.client.reactive.ReactiveRestClients
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate


@Configuration
class EsConfig(private val esProperties: EsProperties) {

    @Bean
    fun reactiveElasticsearchClient(): ReactiveElasticsearchClient {
        val clientConfiguration: ClientConfiguration = ClientConfiguration.builder()
                .connectedTo(esProperties.endpoints!!)
                .withConnectTimeout(esProperties.connectionTimeout!!)
                .withSocketTimeout(esProperties.socketTimeout!!)
                .withWebClientConfigurer { webClient ->
                    val exchangeStrategies = ExchangeStrategies.builder().build()
                    webClient.mutate().exchangeStrategies(exchangeStrategies).build()
                }
                .build()
        return ReactiveRestClients.create(clientConfiguration)
    }

    @Bean
    fun elasticsearchConverter(): ElasticsearchConverter {
        return MappingElasticsearchConverter(elasticsearchMappingContext())
    }

    @Bean
    fun elasticsearchMappingContext(): SimpleElasticsearchMappingContext {
        return SimpleElasticsearchMappingContext()
    }

    @Bean
    fun reactiveElasticsearchOperations(): ReactiveElasticsearchOperations {
        return ReactiveElasticsearchTemplate(reactiveElasticsearchClient(), elasticsearchConverter())
    }
}
