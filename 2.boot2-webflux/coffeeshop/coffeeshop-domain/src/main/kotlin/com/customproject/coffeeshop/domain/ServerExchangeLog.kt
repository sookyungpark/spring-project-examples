package com.customproject.coffeeshop.domain


enum class ServerExchangeCode(val value: String) {
    SUCCESS("SUCC"), FAILURE("FAIL")
}

data class ServerExchangeLog(private val code: ServerExchangeCode,
                             private val uri: String?,
                             private val method: String?,
                             private val clientIp: String?,
                             private val params: Map<String, String?>?,
                             private val headers: Map<String, String?>?,
                             private val statusCode: Int?,
                             private val exceptionCode: String?) {

    override fun toString(): String {
        return toString("|")
    }

    fun toString(separator: String): String {
        return this.code.value +
                separator + (this.uri?: "") +
                separator + (this.method?: "") +
                separator + (this.clientIp?: "") +
                separator + (this.params?.asSequence()
                ?.joinToString(",") { it.key + "=" + (it.value?: "") }
                ?: "") +
                separator + (this.headers?.asSequence()
                ?.joinToString(",") { it.key + "=" + (it.value?: "") }
                ?: "") +
                separator + (this.statusCode?.toString()?: "") +
                separator + (this.exceptionCode?: "")
    }
}
