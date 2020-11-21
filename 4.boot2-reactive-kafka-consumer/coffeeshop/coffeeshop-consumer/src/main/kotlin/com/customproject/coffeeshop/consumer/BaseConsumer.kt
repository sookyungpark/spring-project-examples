package com.customproject.coffeeshop.consumer

import com.customproject.coffeeshop.consumer.config.property.KafkaConsumerProperties
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import reactor.core.Disposable
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import java.time.Duration
import java.util.*
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

abstract class BaseConsumer(private val consumerProperties: KafkaConsumerProperties,
                            private val scheduler: Scheduler) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }
    private var disposable: Disposable? = null

    @PostConstruct
    private fun registerConsumer() {
        val receiverOptions = ReceiverOptions.create<Int, String>().apply {
            consumerProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerProperties.bootstrapServers!!)
            consumerProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumerProperties.autoOffsetReset!!)
            consumerProperty(ConsumerConfig.GROUP_ID_CONFIG, consumerProperties.groupId!!)
            consumerProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
            consumerProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
        }.let {
            it.subscription(Collections.singleton(consumerProperties.topic!!))
                    .addAssignListener { logger.info { "[$it] partition assigned" } }
                    .addRevokeListener { logger.info { "[$it] partition revoked" } }
                    .commitInterval(Duration.ZERO)
        }

        disposable = KafkaReceiver.create(receiverOptions).receive()
                .flatMap { record -> onMessage(record.value())
                        .onErrorContinue(Exception::class.java) { t, _ ->
                            logger.error(t) {
                                "[${record.partition()}][${record.offset()}] unexpected consumer error"
                            }
                        }
                        .flatMap { record.receiverOffset().commit() }
                }
                .subscribeOn(scheduler)
                .subscribe()
    }

    @PreDestroy
    fun preDestroy() {
        disposable?.dispose()
    }

    protected abstract fun onMessage(msg: String): Mono<Void>
}