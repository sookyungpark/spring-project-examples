package com.customproject.coffeeshop.api.support

class CoffeeshopConstants {
    companion object {
        const val HEADER_COFFEESHOP_USER_TOKEN = "X-Coffeeshop-User-Token"
        const val HEADER_COFFEESHOP_REQUEST_ID = "X-Coffeeshop-RID"

        const val HEADER_COFFEESHOP_INTERNAL_API_KEY = "X-Coffeeshop-Secret-Key"
        const val HEADER_COFFEESHOP_ERROR_KEY = "X-Coffeeshop-Error"

        const val TAG_VALUE_UNKNOWN_RESOURCE = "unknown"
        const val ATTR_USERID_KEY = "__UID__"

        val IP_HEADER_CANDIDATES = arrayOf(
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR")
    }
}

public enum class FilterOrder(val value: Int) {
    INTERNAL_AUTH_CHECK_FILTER(1), USER_AUTH_CHECK_FILTER(2), REQUEST_LOGGING_FILTER(100)
}
