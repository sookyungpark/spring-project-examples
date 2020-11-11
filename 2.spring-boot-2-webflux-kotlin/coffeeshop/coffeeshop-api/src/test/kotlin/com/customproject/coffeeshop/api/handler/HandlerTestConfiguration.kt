package com.customproject.coffeeshop.api.handler

import com.customproject.coffeeshop.api.client.ExternalAuthClient
import com.customproject.coffeeshop.api.filter.InternalAuthCheckFilter
import com.customproject.coffeeshop.api.filter.UserAuthCheckFilter
import com.customproject.coffeeshop.api.handler.internal.InternalOrderHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HandlerTestConfiguration {

    @MockBean
    val warmupHandler: WarmupHandler? = null

    @MockBean
    val managementHandler: ManagementHandler? = null

    @MockBean
    val orderHandler: OrderHandler? = null

    @MockBean
    val userHandler: UserHandler? = null

    @MockBean
    val menuHandler: MenuHandler? = null

    @MockBean
    val internalOrderHandler: InternalOrderHandler? = null

    @MockBean
    val userAuthCheckFilter: UserAuthCheckFilter? = null

    @MockBean
    val internalAuthCheckFilter: InternalAuthCheckFilter? = null

}
