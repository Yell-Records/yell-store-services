package com.yellrecords.services.user.dto

data class RegistrationInfo(
    val username: String,
    val rawPassword: String,
    val email: String? = null,
)
