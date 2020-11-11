package com.customproject.coffeeshop.domain

data class Cost(val currency: Currency,
                val value: Float)

enum class Currency {
    USD
}