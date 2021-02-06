package com.customproject.coffeeshop.api.service

import com.customproject.coffeeshop.api.client.ExternalProfileClient
import com.customproject.coffeeshop.api.repository.UserRepository
import com.customproject.coffeeshop.domain.response.UserListResponse
import com.customproject.coffeeshop.domain.response.UserProfileGetResponse
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Service
class UserService(private val externalProfileClient: ExternalProfileClient,
                  @Qualifier("MockUserRepository") private val userRepository: UserRepository) {

    companion object {
        private val log = KotlinLogging.logger {}
    }

    fun list(countLimit: Long): Flux<UserListResponse> {
        return userRepository.list()
                .map {
                    UserListResponse(id = it)
                }
                .limitRequest(countLimit)
    }

    fun getProfile(id: String): Mono<UserProfileGetResponse> {
        return externalProfileClient.get(id)
                .map {
                    UserProfileGetResponse(id = id, name = it.name)
                }
    }
}
