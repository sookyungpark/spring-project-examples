package com.customproject.coffeeshop.api.config.property

abstract class HttpClientProperties {
    abstract var baseUrl: String?
    abstract var connectTimeoutMillis: Int?
    abstract var readTimeoutSec: Int?
    abstract var writeTimeoutSec: Int?
    abstract var soKeepAlive: Boolean?
    abstract var tcpNoDelay: Boolean?
    abstract var reuseAddr: Boolean?
}