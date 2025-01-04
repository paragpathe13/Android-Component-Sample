package com.example.myviewmodelsample.Data

data class UserLoginRequest(
    val expiresInMins: Int,
    val password: String,
    val username: String
)