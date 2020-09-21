package com.customproject.coffeeshop.api.resolver

import com.customproject.coffeeshop.api.domain.Authorized
import com.customproject.coffeeshop.api.domain.AuthorizedMethod
import com.customproject.coffeeshop.api.domain.UserProfile
import mu.KotlinLogging
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class AuthArgumentResolver: HandlerMethodArgumentResolver {
    companion object {
        private val log = KotlinLogging.logger {}
    }

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return UserProfile::class.java.isAssignableFrom(parameter.parameterType)
    }

    @Throws(Exception::class)
    override fun resolveArgument(parameter: MethodParameter, mavContainer: ModelAndViewContainer,
                                 webRequest: NativeWebRequest?, binderFactory: WebDataBinderFactory?): Any {
        val authorized = parameter.getParameterAnnotation(Authorized::class.java)
        val authorizedMethods = parameter.getParameterAnnotation(AuthorizedMethod::class.java)

        return UserProfile(userKey = "dummy")
    }
}