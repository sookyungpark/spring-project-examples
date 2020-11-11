package com.customproject.coffeeshop.domain


enum class ServerExchangeCode(val value: String) {
    SUCCESS("SUCCESS"), FAILURE("FAILURE")
}

data class ServerExchangeLog(private val code: ServerExchangeCode,
                             private val uri: String?,
                             private val method: String?,
                             private val clientIp: String?,
                             private val statusCode: Int?) {

    override fun toString(): String {
        return toString("|")
    }

    fun toString(separator: String): String {
        return this.code.value +
                separator + (this.uri?: "") +
                separator + (this.method?: "") +
                separator + (this.clientIp?: "") +
                separator + (this.statusCode?.toString()?: "")
    }
}
