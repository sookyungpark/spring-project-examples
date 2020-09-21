package com.customproject.coffeeshop.api.domain

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(RetentionPolicy.RUNTIME)
annotation class Internal
