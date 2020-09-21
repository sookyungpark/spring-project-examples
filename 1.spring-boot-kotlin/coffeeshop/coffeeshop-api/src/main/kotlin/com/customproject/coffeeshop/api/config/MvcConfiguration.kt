package com.customproject.coffeeshop.api.config

import com.customproject.coffeeshop.api.config.property.ApplicationProperties
import com.customproject.coffeeshop.api.config.property.HttpClientProperties
import com.customproject.coffeeshop.api.resolver.AuthArgumentResolver
import com.customproject.coffeeshop.api.support.HostNameSupport
import com.customproject.coffeeshop.api.support.PrometheusMeterRegistries
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.binder.system.UptimeMetrics
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.apache.http.client.config.RequestConfig
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.socket.ConnectionSocketFactory
import org.apache.http.conn.socket.PlainConnectionSocketFactory
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.ssl.SSLContexts
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.MediaType
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import springfox.documentation.annotations.ApiIgnore
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import java.security.KeyManagementException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException

@Configuration
class MvcConfiguration(private val appProperties: ApplicationProperties,
                       private val httpClientProperties: HttpClientProperties,
                       private val authArgumentResolver: AuthArgumentResolver): WebMvcConfigurerAdapter() {

    override fun configureContentNegotiation(configurer: ContentNegotiationConfigurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON)
    }

    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
        argumentResolvers.add(authArgumentResolver)
        super.addArgumentResolvers(argumentResolvers)
    }

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
    fun swaggerConfig() : Docket = Docket(DocumentationType.SWAGGER_12)
            .ignoredParameterTypes(ApiIgnore::class.java)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.customproject.coffeeshop.api"))
            .paths(PathSelectors.regex("/.*"))
            .build()

    @Bean
    @Throws(KeyStoreException::class, NoSuchAlgorithmException::class, KeyManagementException::class)
    fun httpClient(): CloseableHttpClient {
        val defaultClientProperties = httpClientProperties.default!!

        val sslContext = SSLContexts.custom()
                .loadTrustMaterial(null) { chain, authType -> true }
                .build()
        val sslsf = SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE)

        val registry = RegistryBuilder.create<ConnectionSocketFactory>()
                .register("http", PlainConnectionSocketFactory())
                .register("https", sslsf)
                .build()

        val cm = PoolingHttpClientConnectionManager(registry)
        cm.maxTotal = 50

        val requestConfig = RequestConfig.custom()
                .setConnectTimeout(defaultClientProperties.connectTimeoutMillis!!)
                .setMaxRedirects(1)
                .setSocketTimeout(defaultClientProperties.socketTimeoutMillis!!).build()

        return HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(cm)
                .build()
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
        FileDescriptorMetrics().bindTo(meterRegistry)
        UptimeMetrics().bindTo(meterRegistry)

        return meterRegistry
    }
}
