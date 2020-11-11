package com.customproject.coffeeshop.domain.response

data class UserListResponse(val id: String)

data class UserListOrderResponse(val id: String)

data class UserProfileGetResponse(val id: String, var name: String)
