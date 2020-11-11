package com.customproject.coffeeshop.api.support

import com.customproject.coffeeshop.api.domain.RequestContext
import org.springframework.web.context.request.NativeWebRequest
import javax.servlet.http.HttpServletRequest

class RequestContextSupport {
    companion object {
        fun getRequestContext(request: NativeWebRequest): RequestContext {
            return RequestContext(
                    uri = request.getDescription(false),
                    method = "",
                    clientIp = getClientIp(request)?: ""
            )
        }

        fun getRequestContext(request: HttpServletRequest): RequestContext {
            return RequestContext(
                    uri = request.requestURI,
                    method = "",
                    clientIp = getClientIp(request)?: ""
            )
        }

        fun getClientIp(request: NativeWebRequest): String? {
            try {
                for (header in CoffeeshopConstants.IP_HEADER_CANDIDATES) {
                    request.getHeader(header)?.let {
                        if (it.isNotEmpty()) {
                            return it
                        }
                    }
                }
            } catch (e: Exception) {
            }
            return null
        }

        fun getClientIp(request: HttpServletRequest): String? {
            try {
                for (header in CoffeeshopConstants.IP_HEADER_CANDIDATES) {
                    request.getHeader(header)?.let {
                        if (it.isNotEmpty()) {
                            return it
                        }
                    }
                }
            } catch (e: Exception) {
            }
            return null
        }
    }
}