package com.customproject.coffeeshop.consumer.config.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "order")
data class OrderProperties(var consumer: OrderConsumerProperties? = OrderConsumerProperties())

data class OrderConsumerProperties(override var bootstrapServers: String? = "localhost:9200",
                                   override var topic: String? = "order-v1",
                                   override var groupId: String? = "coffeeshop-consumer",
                                   override var autoOffsetReset: String? = "latest") : KafkaConsumerProperties()