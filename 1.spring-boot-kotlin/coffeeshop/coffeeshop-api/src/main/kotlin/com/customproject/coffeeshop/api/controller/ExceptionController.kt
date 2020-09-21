package com.customproject.coffeeshop.api.controller

import com.customproject.coffeeshop.api.domain.CommonErrorResponse
import com.customproject.coffeeshop.api.support.RequestContextSupport
import lombok.extern.slf4j.Slf4j
import mu.KotlinLogging
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.context.request.NativeWebRequest
import java.lang.IllegalStateException

@Slf4j
@ControllerAdvice
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
class ExceptionController {
    companion object {
        private val log = KotlinLogging.logger {}
    }

    @ExceptionHandler(IllegalStateException::class)
    @ResponseBody
    fun illegalStateException(e: IllegalStateException, request: NativeWebRequest): ResponseEntity<CommonErrorResponse> {
        logWithRequestContext(e, request)
        return ResponseEntity(CommonErrorResponse(HttpStatus.BAD_REQUEST.value().toString(), HttpStatus.BAD_REQUEST.reasonPhrase), HttpStatus.BAD_REQUEST)
    }

    fun logWithRequestContext(e: Exception, request: NativeWebRequest) {
        val requestContext = RequestContextSupport.getRequestContext(request)
        log.error(e.cause) { "request failed : message=${e.message}, requestContext=$requestContext"}
    }
}