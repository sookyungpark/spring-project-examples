package com.customproject.coffeeshop.consumer.support

import java.util.*

class OrderSupport {
    companion object {
        private const val ID_PREFIX = "ord::"
        fun generateId() = ID_PREFIX +  UUID.randomUUID()
    }
}