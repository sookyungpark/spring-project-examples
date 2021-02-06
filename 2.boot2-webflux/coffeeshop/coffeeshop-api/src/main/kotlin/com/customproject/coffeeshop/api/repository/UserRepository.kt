package com.customproject.coffeeshop.api.repository

import com.customproject.coffeeshop.api.exception.NotFoundException
import com.customproject.coffeeshop.domain.User
import com.customproject.coffeeshop.domain.UserEntity
import com.customproject.coffeeshop.domain.UserTier
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers


interface UserRepository {
    fun list(page: Int = 0, size: Int = 100): Flux<String>
    fun get(id: String): Mono<User>
}

@Repository
@Qualifier(value = "MockUserRepository")
class MockUserRepository : UserRepository {

    override fun list(page: Int, size: Int): Flux<String> {
        return Flux.fromArray(arrayOf("user1", "user2"))
    }

    override fun get(id: String): Mono<User> {
        return Mono.just(User(
                id = "userid1",
                name = "username1",
                tier = UserTier.BRONZE
        ))
    }
}

@Repository
@Qualifier(value = "ReactiveJpaUserRepository")
class ReactiveJpaUserRepository(private val jpaUserRepository: JpaUserRepository) : UserRepository {

    override fun list(page: Int, size: Int): Flux<String> {
        return Flux.fromIterable(jpaUserRepository.findAll(PageRequest.of(page, size)))
                .map { it.id }
                .subscribeOn(Schedulers.boundedElastic())
    }

    override fun get(id: String): Mono<User> {
        return Mono.fromCallable { jpaUserRepository.findById(id).orElseThrow { NotFoundException("user not found") } }
                .map { User(id = it.id, name = it.name, tier = it.tier) }
    }
}

@Transactional
interface JpaUserRepository : JpaRepository<UserEntity, String>