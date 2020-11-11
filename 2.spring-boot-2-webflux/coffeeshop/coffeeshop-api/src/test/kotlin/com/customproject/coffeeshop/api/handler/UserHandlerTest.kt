package com.customproject.coffeeshop.api.handler

import com.customproject.coffeeshop.api.filter.UserAuthCheckFilter
import com.customproject.coffeeshop.api.support.CoffeeshopConstants
import com.customproject.coffeeshop.domain.response.UserProfileGetResponse
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.given
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.snippet.Attributes
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono


class UserHandlerTest: BaseHandlerTest() {

    @Autowired
    private val userHandler: UserHandler? = null

    @Autowired
    private val userAuthCheckFilter: UserAuthCheckFilter? = null

    @Test
    fun getProfile() {
        // given
        val userId = "userId"
        val expectedBody = UserProfileGetResponse(
                id = userId,
                name = "username")

        val response = ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(expectedBody), UserProfileGetResponse::class.java)

        // mock
        given(this.userHandler!!.getProfile(any())).willReturn(response)

        given(this.userAuthCheckFilter!!.filter(any(), any())).willReturn(response)
    //    given(this.userAuthCheckFilter.order).willReturn(FilterOrder.USER_AUTH_CHECK_FILTER.value)

        // validate
        webTestClient!!.get().uri("/api/users/profile")
                .header(CoffeeshopConstants.HEADER_COFFEESHOP_USER_TOKEN, "<user-token>")
                .exchange()
                .expectStatus().isOk
                .expectBody().json(objectMapper.writeValueAsString(expectedBody))
                .consumeWith(
                        WebTestClientRestDocumentation.document(
                                "get-user-profile",
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                HeaderDocumentation.requestHeaders(
                                        HeaderDocumentation.headerWithName(CoffeeshopConstants.HEADER_COFFEESHOP_USER_TOKEN).description(
                                                "user token"
                                        )
                                                .attributes(Attributes.key("required").value(true))
                                                .attributes(Attributes.key("constraints").value("Must not be null. Must not be empty."))
                                ),
                                PayloadDocumentation.responseFields(
                                        PayloadDocumentation.fieldWithPath("id").description("id").optional(),
                                        PayloadDocumentation.fieldWithPath("name").description("name").optional())
                        )
                )
    }
}
