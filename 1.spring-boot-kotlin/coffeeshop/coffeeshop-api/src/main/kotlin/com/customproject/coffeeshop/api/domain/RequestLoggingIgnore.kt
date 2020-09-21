package com.customproject.coffeeshop.api.domain

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
annotation class RequestLoggingIgnore