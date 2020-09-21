package com.customproject.coffeeshop.api.client

import com.customproject.coffeeshop.api.exception.UnauthorizedException
import com.customproject.coffeeshop.api.support.CoffeeshopConstants
import com.customproject.coffeeshop.domain.response.external.ExternalProfileGetResponse
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap


@Component
class ExternalProfileClient {

    @Throws(UnauthorizedException::class)
    fun get(userId: String): ExternalProfileGetResponse {
        return ExternalProfileGetResponse("profile1")
    }
}
