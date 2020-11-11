package com.customproject.coffeeshop.api.aspect

import com.customproject.coffeeshop.api.exception.InternalApiAuthException
import com.customproject.coffeeshop.api.support.InternalWhitelistSupport
import com.customproject.coffeeshop.api.support.RequestContextSupport
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Aspect
@Component
class InternalApiAspect(val internalWhitelistSupport: InternalWhitelistSupport) {

    @Before("execution(public * com.customproject.coffeeshop.api.controller..*.*(..)) && @annotation(com.customproject.coffeeshop.api.domain.Internal)")
    @Throws(Throwable::class)
    fun before(joinPoint: JoinPoint) {
        val attr = RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes
        val request = attr.request

        val clientIpAddress = RequestContextSupport.getClientIp(request)
        if (clientIpAddress != null && !internalWhitelistSupport.allow(clientIpAddress)) {
            throw InternalApiAuthException("ip $clientIpAddress is not allowed for internal api")
        }
    }
}