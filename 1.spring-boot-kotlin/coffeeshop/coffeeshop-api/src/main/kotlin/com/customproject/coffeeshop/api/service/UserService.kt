package com.customproject.coffeeshop.api.service

import com.customproject.coffeeshop.api.client.ExternalProfileClient
import com.customproject.coffeeshop.api.repository.UserRepository
import com.customproject.coffeeshop.domain.response.UserListResponse
import com.customproject.coffeeshop.domain.response.UserProfileGetResponse
import mu.KotlinLogging
import org.springframework.stereotype.Service


@Service
class UserService(private val externalProfileClient: ExternalProfileClient,
                  private val userRepository: UserRepository) {

    companion object {
        private val log = KotlinLogging.logger {}
    }

    fun list(): List<UserListResponse> {
        return userRepository.list()
                .map { UserListResponse(id = it) }
    }

    fun getProfile(id: String): UserProfileGetResponse {
        return externalProfileClient.get(id)
                .let { UserProfileGetResponse(id = id, name = it.name) }
    }
}
