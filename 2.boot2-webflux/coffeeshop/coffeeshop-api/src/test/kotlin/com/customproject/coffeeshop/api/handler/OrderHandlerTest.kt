package com.customproject.coffeeshop.api.handler

import com.customproject.coffeeshop.api.support.CoffeeshopConstants
import com.customproject.coffeeshop.domain.request.CreateOrderMenuRequest
import com.customproject.coffeeshop.domain.request.CreateOrderRequest
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.given
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.snippet.Attributes
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerResponse


class OrderHandlerTest(): BaseHandlerTest() {

    @Autowired
    private val orderHandler: OrderHandler? = null

    @Test
    fun create() {

        val menuRequestBody = CreateOrderMenuRequest(menuId = "menuId", count = 1)
        val requestBody = CreateOrderRequest(menus = listOf(menuRequestBody))

        // given
        val response = ServerResponse.noContent().build()

        // mock
        given(this.orderHandler!!.create(any())).willReturn(response)

        // validate
        webTestClient!!.post().uri("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .header(CoffeeshopConstants.HEADER_COFFEESHOP_USER_TOKEN, "<user-token>")
                .body(BodyInserters.fromObject(requestBody))
                .exchange()
                .expectStatus().isNoContent
                .expectBody()
                .consumeWith(
                        WebTestClientRestDocumentation.document(
                                "create-order",
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                HeaderDocumentation.requestHeaders(
                                        HeaderDocumentation.headerWithName(CoffeeshopConstants.HEADER_COFFEESHOP_USER_TOKEN).description(
                                                "user token"
                                        )
                                                .attributes(Attributes.key("required").value(false))
                                                .attributes(Attributes.key("constraints").value("If exists, response includes country-specific datas, including common ones. If not exist, response includes only common ones"))
                                                .optional()
                                ),
                                requestFields(
                                        fieldWithPath("menus").description("menus"),
                                        fieldWithPath("menus[].menuId").description("menu id"),
                                        fieldWithPath("menus[].count").description("menu count")
                                )
                        )
                )
    }
}
