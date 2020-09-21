package com.customproject.coffeeshop.api.support

class CoffeeshopConstants {
    companion object {
        const val HEADER_COFFEESHOP_REQUEST_ID = "X-Coffeeshop-RID"

        const val ATTR_TRANSACTION_ID_KEY = "__TID__"
        const val ATTR_RESOURCE_ID_KEY = "__RID__"

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
