package com.customproject.coffeeshop.domain

data class User(val id: String,
                val name: String,
                val tier: UserTier)

enum class UserTier {
    BRONZE, SILVER, GOLD
}

data class UserEntity(val id: String,
                      val name: String,
                      val tier: UserTier)