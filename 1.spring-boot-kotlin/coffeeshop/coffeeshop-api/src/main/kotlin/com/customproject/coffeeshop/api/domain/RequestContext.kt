package com.customproject.coffeeshop.api.domain

data class RequestContext(val uri: String?,
                          val method: String?,
                          val clientIp: String)