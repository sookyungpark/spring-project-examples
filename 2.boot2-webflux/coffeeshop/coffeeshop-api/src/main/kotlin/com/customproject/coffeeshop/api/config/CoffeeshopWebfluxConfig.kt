package com.customproject.coffeeshop.api.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.customproject.coffeeshop.api.config.property.*
import com.customproject.coffeeshop.api.filter.ExternalRequestLoggingFilter
import com.customproject.coffeeshop.api.support.HostNameSupport
import com.customproject.coffeeshop.api.support.PrometheusMeterRegistries
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.binder.system.UptimeMetrics
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.boot.autoconfigure.web.ResourceProperties
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Primary
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.config.*
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.tcp.TcpClient


@Configuration
@EnableWebFlux
public class CoffeeshopWebfluxConfig(private val appProperties: ApplicationProperties,
                                     private val authProperties: AuthProperties,
                                     private val profileProperties: ProfileProperties) : WebFluxConfigurer {

    @Bean
    @Primary
    fun objectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.registerModules(AfterburnerModule(), JavaTimeModule())
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        mapper.configure(SerializationFeature.INDENT_OUTPUT, "RELEASE" != appProperties.phase)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.registerModule(KotlinModule())
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        return mapper
    }

    @Bean
    fun errorAttributes(): ErrorAttributes {
        return DefaultErrorAttributes()
    }

    @Bean
    fun resourceProperties(): ResourceProperties {
        return ResourceProperties()
    }

    @Primary
    @Bean(name = ["meterRegistry"])
    fun getMeterRegistry(): PrometheusMeterRegistry {
        val meterRegistry = PrometheusMeterRegistries.newRegistry()
        meterRegistry.config().commonTags("handler", appProperties.phase, "hostname", HostNameSupport.getLocalhost())

        JvmMemoryMetrics().bindTo(meterRegistry)
        JvmGcMetrics().bindTo(meterRegistry)
        JvmThreadMetrics().bindTo(meterRegistry)
        ProcessorMetrics().bindTo(meterRegistry)
        ClassLoaderMetrics().bindTo(meterRegistry)

        return meterRegistry
    }

    @Bean(name = ["authClient"])
    @DependsOn(value = ["meterRegistry"])
    fun authClient(meterRegistry: PrometheusMeterRegistry): WebClient {
        return getWebClient(authProperties.httpClient!!, "auth", meterRegistry)
    }

    @Bean(name = ["profileClient"])
    @DependsOn(value = ["meterRegistry"])
    fun profileClient(meterRegistry: PrometheusMeterRegistry): WebClient {
        return getWebClient(profileProperties.httpClient!!, "profile", meterRegistry)
    }

    private fun getWebClient(properties: HttpClientProperties, metricTag: String, meterRegistry: PrometheusMeterRegistry) : WebClient {
       val tcpClient = TcpClient.create()
               .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.connectTimeoutMillis)
               .option(ChannelOption.SO_KEEPALIVE, properties.soKeepAlive)
               .option(ChannelOption.TCP_NODELAY, properties.tcpNoDelay)
               .option(ChannelOption.SO_REUSEADDR, properties.reuseAddr)
               .doOnConnected { connection ->
                   connection
                           .addHandlerLast(ReadTimeoutHandler(properties.readTimeoutSec!!))
                           .addHandlerLast(WriteTimeoutHandler(properties.writeTimeoutSec!!))
               }

        return  WebClient.builder()
                .clientConnector(ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                .baseUrl(properties.baseUrl!!)
                .filter(ExternalRequestLoggingFilter(metricTag, meterRegistry))
                .build()
    }
}
