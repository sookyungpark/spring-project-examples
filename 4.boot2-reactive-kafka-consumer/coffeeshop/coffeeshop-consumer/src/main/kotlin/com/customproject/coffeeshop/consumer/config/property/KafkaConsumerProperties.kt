package com.customproject.coffeeshop.consumer.config.property

abstract class KafkaConsumerProperties {
    abstract var bootstrapServers: String?
    abstract var topic: String?
    abstract var groupId: String?
    abstract var autoOffsetReset: String?
}