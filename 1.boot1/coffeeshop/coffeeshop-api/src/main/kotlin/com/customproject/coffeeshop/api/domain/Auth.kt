package com.customproject.coffeeshop.api.domain

@Target(AnnotationTarget.VALUE_PARAMETER)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class Authorized(val forced: Boolean = false, val permissionType: String = "NONE")

@Target(AnnotationTarget.VALUE_PARAMETER)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class AuthorizedMethod(val authMethod: String = "TOKEN")

data class AuthContext(val appId: String,
                       val tokenString: String)
