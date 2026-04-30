package com.yellrecords.services.auth.dto

data class LoginRequest(
    val username: String,
    val rawPassword: String,
)
