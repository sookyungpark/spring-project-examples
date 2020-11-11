package com.customproject.coffeeshop.api.aspect

import com.customproject.coffeeshop.api.domain.RequestLoggingIgnore
import com.customproject.coffeeshop.api.support.RequestContextSupport
import mu.KotlinLogging
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import org.jetbrains.kotlin.backend.common.push
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import javax.servlet.http.HttpServletRequest

@Aspect
@Component
class RequestLoggingAspect {
    companion object {
        private const val SEPARATOR = "|"
        private val requestLogger = KotlinLogging.logger("request-logger")
    }

    @Before("execution(public * com.customproject.coffeeshop.api.controller..*.*(..)) || @annotation(com.customproject.coffeeshop.api.domain.RequestLogging)")
    @Throws(Throwable::class)
    fun before(joinPoint: JoinPoint) {

        val attr = RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes
        val request = attr.request

        val requestLog = RequestContextSupport.getRequestContext(request)
        val argumentLog = getRequestArgs(joinPoint).joinToString(SEPARATOR)

        requestLogger.info { requestLog.toString() + SEPARATOR + argumentLog }
    }

    private fun getRequestArgs(joinPoint: JoinPoint): List<String> {
        val filteredArgs = mutableListOf<String>()
        val args = joinPoint.args?: return emptyList()

        val methodSignature = joinPoint.signature as MethodSignature
        val annotationMatrix = methodSignature.method.parameterAnnotations ?: return emptyList()

        args.forEachIndexed { index, arg ->
            if (!isRequestIgnored(index, annotationMatrix)) {
                arg?.let { filteredArgs.push(it.toString()) }
            }
        }
        return filteredArgs
    }

    private fun isRequestIgnored(index: Int, annotationMatrix: Array<Array<Annotation?>?>): Boolean {
        val annotationGroup = annotationMatrix.getOrNull(index)
        annotationGroup?.forEach {
            if(it is RequestLoggingIgnore) {
                return true
            }
        }
        return false
    }
}
