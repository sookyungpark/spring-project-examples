package com.customproject.coffeeshop.api.repository

import com.customproject.coffeeshop.api.config.property.ApplicationProperties
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux


interface UserRepository {
    fun list(useCache: Boolean = true): Flux<String>
}

@Repository
@Qualifier(value = "MockUserRepository")
class MockUserRepository(private val appProperties: ApplicationProperties) : UserRepository {

    override fun list(useCache: Boolean): Flux<String> {
        return Flux.fromArray(arrayOf("user1", "user2"))
    }
}