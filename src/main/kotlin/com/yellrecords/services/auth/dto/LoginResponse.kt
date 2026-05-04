package com.yellrecords.services.auth.dto

data class LoginResponse(
    val token: String,
    val username: String,
)
