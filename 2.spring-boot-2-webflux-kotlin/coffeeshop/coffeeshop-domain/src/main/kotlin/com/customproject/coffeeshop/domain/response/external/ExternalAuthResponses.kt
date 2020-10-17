package com.customproject.coffeeshop.domain.response.external

data class ExternalAuthUserResponse(val userKey: String,
                                    val createdTime: Long,
                                    val expireTime: Long,
                                    val signedIn: Boolean)